//
//  File.swift
//  
//
//  Created by Payton Curry on 6/2/24.
//

import Foundation
import SwiftUI
import SharedQProtocol

struct GroupConfirmationView: View {
    @Environment(\.dismiss) var dismiss
    var group: SQGroup
    @Environment(FIRManager.self) var firManager
    @State var showingQueue = false
    var body: some View {
        ScrollView {
            VStack {
                Text(group.name).font(.largeTitle).fontWeight(.semibold)
                Text("is currently listening to").font(.title2).fontWeight(.medium)
                
                VStack {
                    AsyncImage(url: group.currentlyPlaying?.albumArt) { img in
                        img.resizable().aspectRatio(contentMode: .fit).cornerRadius(15.0).frame(width: 250, height: 250)
                    } placeholder: {
                        Image("mediaItemPlaceholder", bundle: .module, label: Text("Placeholder cover art")).resizable().aspectRatio(contentMode: .fit).cornerRadius(15.0)
                    }.padding(.horizontal, 30)
                    Text(group.currentlyPlaying?.title ?? "Nothing playing").font(.title2).fontWeight(.medium)
                    Text(group.currentlyPlaying?.artist ?? "Nobody").opacity(0.9).font(.title3)
                }
                VStack {
                    Text("Are you sure you want to join?").font(.title2).fontWeight(.semibold)
                    Text("everybody currently in \"\(group.name)\" will be able to see that you joined, and will be able to see your profile. if you would like to know who is currently listening in this group, press “View Queue” (nobody will know that you’re viewing the queue). once you join, the music in the queue will start playing immediately.").opacity(0.8)
                }.padding(.top)
                VStack {
                    HStack {
                        Button(action: {
                            dismiss()
                        }, label: {
                            Text("Cancel")
                        }).buttonStyle(.bordered).padding(.horizontal)
                        
                        Button(action: {
                            firManager.syncManager.connectToGroup(group: group, token: firManager.authToken!)
                            dismiss()
                        }, label: {
                            Text("Join")
                        }).buttonStyle(.borderedProminent).padding(.horizontal)
                        
                    }
                    Button(action: {
                        showingQueue = true
                    }, label: {
                        Text("View Queue")
                    }).buttonStyle(.bordered).padding(.horizontal)
                }.font(.title2)
            }.navigationTitle("Join \(group.name)?").navigationBarBackButtonHidden().navigationBarTitleDisplayMode(.inline).padding().sheet(isPresented: $showingQueue, content: {
                PreviewQueueView(group: group)
            })
        }
    }
}
