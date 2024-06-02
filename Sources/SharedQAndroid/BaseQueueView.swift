//
//  File.swift
//  
//
//  Created by Payton Curry on 6/2/24.
//

import Foundation
import SharedQProtocol
import SwiftUI

struct BaseQueueView: View {
    var group: SQGroup
    var body: some View {
        List {
            ForEach(group.previewQueue) { item in
                HStack {
                    AsyncImage(url: item.song.albumArt) { img in
                        img.resizable().aspectRatio(contentMode: .fit).cornerRadius(5.0).frame(width: 40)
                    } placeholder: {
                        Image("mediaItemPlaceholder", bundle: .module, label: Text("Placeholder cover art")).aspectRatio(contentMode: .fit).cornerRadius(5.0).frame(width: 40)
                    }.padding(.trailing)
                    VStack(alignment: .leading) {
                        Text(item.song.title).font(.title2)
                        Text(item.song.artist).opacity(0.5).font(.title3)
                    }
                    Spacer()
                    Text("Added by \(item.addedBy)").opacity(0.5)
                }
            }
            if group.previewQueue.isEmpty {
                Text("No songs in queue!").font(.title).opacity(0.5)
            }
            VStack(alignment: .leading) {
                Text("Members (\(group.members.count)):").font(.title).fontWeight(.semibold)
                ForEach(group.members) { member in
                    HStack {
                        if group.connectedMembers.contains(where: {$0.id == member.user.id}) {
                            Circle().frame(width: 10, height: 10).foregroundColor(.green)
                        } else {
                            Circle().frame(width: 10, height: 10).foregroundColor(.gray)
                        }
                        Text(member.user.username).font(.title2).fontWeight(.medium)
                        if member.isOwner {
                            Image(systemName: "star.fill").foregroundStyle(.yellow)
                        }
                        
                        
                    }
                }
            }
        }
    }
}
