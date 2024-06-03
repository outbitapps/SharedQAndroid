//
//  SwiftUIView.swift
//
//
//  Created by Payton Curry on 6/2/24.
//

import SharedQProtocol
import SwiftUI

struct GroupConnectedView: View {
    @Environment(FIRManager.self) var firManager
    @State var showingQueueView = false
    var body: some View {
        if let group = firManager.connectedGroup {
            VStack {
                VStack {
                    Text(group.name).font(.title).fontWeight(.semibold)
                    Text("\(group.connectedMembers.count) listening ∙ \(group.publicGroup ? "Public" : "Private") Group")
                }
                AsyncImage(url: group.currentlyPlaying?.albumArt) { img in
                    img.resizable().aspectRatio(contentMode: .fit).cornerRadius(15.0).frame(width: 300, height: 300)
                } placeholder: {
                    Image("mediaItemPlaceholder", bundle: .module, label: Text("Placeholder cover art")).resizable().aspectRatio(contentMode: .fit).cornerRadius(15.0)
                }.padding(.horizontal, 10)
                VStack(alignment: .leading, content: {
                    Text(group.currentlyPlaying?.title ?? "Nothing playing").font(.title).fontWeight(.medium)
                    Text(group.currentlyPlaying?.artist ?? "Nobody").opacity(0.9).font(.title2)
                })
                HStack {
                    Button(action: {}, label: {
                        Image(systemName: "chevron.left").font(.system(size: 70))
                    })
                    playPauseButton(group: group)
                    Button(action: {}, label: {
                        Image(systemName: "chevron.right").font(.system(size: 70))
                    })
                }
                Spacer()
                HStack {
                    Button(action: {
                        Task {
                            await firManager.syncManager.disconnect()
                        }
                    }, label: {
                        Text("Leave")
                    }).buttonStyle(.bordered).padding(.horizontal)
                    Button(action: {
                        showingQueueView = true
                    }, label: {
                        Text("View Queue")
                    }).buttonStyle(.borderedProminent).padding(.horizontal)
                }
            }.sheet(isPresented: $showingQueueView, content: {
                ConnectedQueueView()
            }).padding()
        } else {
            ProgressView().navigationTitle("Loading...")
        }
    }

    @ViewBuilder func playPauseButton(group: SQGroup) -> some View {
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

struct ConnectedQueueView: View {
    @Environment(FIRManager.self) var firManager
    @State var showingAddToQueueView = false
    var body: some View {
        if let group = firManager.connectedGroup {
            VStack {
                VStack(alignment: .leading) {
                    Text("CURRENTLY PLAYING").font(.callout).fontWeight(.semibold)

                    HStack {
                        AsyncImage(url: group.currentlyPlaying?.albumArt) { img in
                            img.resizable().aspectRatio(contentMode: .fit).cornerRadius(10.0).frame(width: 80)
                        } placeholder: {
                            Image("mediaItemPlaceholder", bundle: .module, label: Text("Placeholder cover art")).resizable().aspectRatio(contentMode: .fit).frame(width: 80).cornerRadius(10.0)
                        }
                        VStack(alignment: .leading) {
                            Text(group.currentlyPlaying?.title ?? "Nothing").font(.title).fontWeight(.medium)
                            Text(group.currentlyPlaying?.artist ?? "Nobody").font(.title2).opacity(0.8)
                        }
                    }
                }.padding().frame(maxWidth: .infinity).frame(height: 130)
                Divider()
                BaseQueueView(group: group).frame(maxHeight: .infinity).overlay(alignment: .bottom) {
                    Button(action: {
                        showingAddToQueueView = true
                    }, label: {
                        Text("+ Add to queue")
                    }).buttonStyle(.borderedProminent)
                }

            }.sheet(isPresented: $showingAddToQueueView, content: {
                AddToQueueView()
            })
        } else {
            ProgressView()
        }
    }
}

struct AddToQueueView: View {
    @Environment(FIRManager.self) var firManager
    @Environment(\.dismiss) var dismiss
    @State var searchQuery = ""
    @State var recentSongs: [SQSong] = []
    @State var searchResults: [SQSong] = []
    @State var loadedRecents = false
    @State var loadedResults = false
    @State var loading = false
    var body: some View {
        VStack {
            HStack {
                TextField("Search a song...", text: $searchQuery)
                if !searchQuery.isEmpty {
                    Button(action: {
                        Task {
                            loadedResults = false
                            searchResults = await musicService.searchFor(searchQuery)
                            loadedResults = true
                        }
                    }, label: {
                        Image(systemName: "magnifyingglass").font(.title)
                    }).transition(.scale).animation(.spring, value: searchQuery)
                }
            }
            List {
                if !searchQuery.isEmpty {
                    Section("Search Results") {
                        if loadedResults {
                            ForEach(searchResults) { song in
                                Button(action: {
                                    Task {
                                        do {
                                            loading = true
                                            try await firManager.syncManager.addToQueue(song:song, user:firManager.currentUser!)
                                            loading = false
                                            dismiss()
                                        } catch {
                                            loading = false
                                            logger.error("error adding \(song.title) to queue :( \(error)")
                                        }
                                    }
                                }, label: {
                                    HStack {
                                        songCell(song: song)
                                        Image(systemName: "plus")
                                    }
                                }).buttonStyle(.plain)
                            }
                        } else {
                            ProgressView()
                        }
                    }
                }
                Section("Recently Played") {
                    if loadedRecents {
                        ForEach(recentSongs) { song in
                            Button(action: {
                                Task {
                                    do {
                                        loading = true
                                        try await firManager.syncManager.addToQueue(song:song, user:firManager.currentUser!)
                                        loading = false
                                        dismiss()
                                    } catch {
                                        loading = false
                                        logger.error("error adding \(song.title) to queue :( \(error)")
                                    }
                                }
                            }, label: {
                                HStack {
                                    songCell(song: song)
                                    Image(systemName: "plus")
                                }
                            }).buttonStyle(.plain)
                        }
                    } else {
                        ProgressView()
                    }
                }
            }.frame(maxHeight: .infinity)
        }.onAppear {
            Task {
                loadedRecents = false
                recentSongs = await musicService.recentlyPlayed()
                loadedRecents = true
            }
        }
    }
    @ViewBuilder func songCell(song: SQSong) -> some View {
        HStack {
            AsyncImage(url: song.albumArt) { img in
                img.resizable().aspectRatio(contentMode: .fit).cornerRadius(5.0).frame(width: 40, height: 40)
            } placeholder: {
                Image("mediaItemPlaceholder", bundle: .module, label: Text("Placeholder cover art")).aspectRatio(contentMode: .fit).cornerRadius(5.0).frame(width: 40, height: 40)
            }.padding(.trailing)
            VStack(alignment: .leading) {
                Text(song.title).font(.title2)
                Text(song.artist).opacity(0.5).font(.title3)
            }
            Spacer()
        }
    }
}
