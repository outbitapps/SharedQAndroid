//
//  SwiftUIView.swift
//  
//
//  Created by Payton Curry on 6/2/24.
//

import SwiftUI
import SharedQProtocol

struct GroupConnectedView: View {
    @Environment(FIRManager.self) var firManager
    var body: some View {
        if let group = firManager.connectedGroup {
                VStack {
                    VStack {
                        Text(group.name).font(.title).fontWeight(.semibold)
                        Text("\(group.connectedMembers.count) listening ∙ \(group.publicGroup ? "Public" : "Private") Group")
                    }
                        AsyncImage(url: group.currentlyPlaying?.albumArt) { img in
                            img.resizable().aspectRatio(contentMode: .fit).cornerRadius(15.0)
                        } placeholder: {
                            Image("mediaItemPlaceholder", bundle: .module, label: Text("Placeholder cover art")).resizable().aspectRatio(contentMode: .fit).cornerRadius(15.0)
                        }.padding(.horizontal, 10)
                    VStack(alignment: .leading, content: {
                        Text(group.currentlyPlaying?.title ?? "Nothing playing").font(.title).fontWeight(.medium)
                        Text(group.currentlyPlaying?.artist ?? "Nobody").opacity(0.9).font(.title2)
                    })
                    HStack {
                        if group.playbackState?.state == PlayPauseState.play {
                            Button(action: {
                                Task {
                                    await firManager.pauseSong()
                                }
                            }, label: {
                                Text("||").font(.system(size: 70))
                            })
                        } else {
                            Button(action: {
                                Task {
                                    await firManager.playSong()
                                }
                            }, label: {
                                Image(systemName: "Icons.Filled.PlayArrow").font(.system(size: 70)).fontDesign(.rounded)
                            })
                        }
                    }
                }
        } else {
            ProgressView().navigationTitle("Loading...")
        }
    }
}

