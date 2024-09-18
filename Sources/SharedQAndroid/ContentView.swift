import Foundation
import SharedQProtocol
import SwiftUI

public struct ContentView: View {
    @AppStorage("needsOnboarding") var needsOnboarding = true
    @Environment(FIRManager.self) var firManager
    @State var groupCreationView = false
    @State var groupBeingCreated: SQGroup? = nil
    @State var loading = false
    @State var showingCreatedView = false
    @State var showGroupConnectedView = false
    @State var showingSettingsSheet = false
    public init() {
    }

    public var body: some View {
        NavigationStack {
            List {
                if let currentUser = firManager.currentUser {
                    ForEach(currentUser.groups) { group in
                        NavigationLink {
                            GroupConfirmationView(group: group)
                        } label: {
                            HomeGroupCell(group: group)
                        }
                    }
                } else {
                    ProgressView()
                }
            }.navigationTitle("Groups").toolbar(content: {
                ToolbarItem {
                    Button(action: {
                        groupCreationView = true
                    }, label: {
                        Image(systemName: "plus")
                    }).buttonStyle(.bordered)
                }
                ToolbarItem {
                    Button(action: {
                        firManager.refreshData()
                    }, label: {
                        Image(systemName: "arrow.clockwise.circle")
                    }).buttonStyle(.bordered).disabled(!firManager.loaded).opacity(!firManager.loaded ? 0.5 : 1.0)
                }
                ToolbarItem {
                    Button(action: {
                        showingSettingsSheet = true
                    }, label: {
                        Image(systemName: "gear")
                    }).buttonStyle(.bordered)
                }
            })
        }.opacity(loading ? 0.5 : 1.0).overlay(content: {
            if loading {
                VStack {
                    ProgressView()
                }
            }
        }).fullScreenCover(isPresented: $needsOnboarding, content: {
            OnboardingAuthView().navigationBarBackButtonHidden()
        }).sheet(isPresented: $groupCreationView, onDismiss: {
            Task {
                if groupBeingCreated != nil {
                    loading = true
                    let recentSong = await musicService.getMostRecentSong()
                    groupBeingCreated?.currentlyPlaying = recentSong
                                    let recents = await musicService.recentlyPlayed()
                                    var queueItems = [SQQueueItem]()
                                    for recent in recents {
                                        queueItems.append(SQQueueItem(song: recent, addedBy: "Payton"))
                                    }
                                    groupBeingCreated?.previewQueue = queueItems
                    _ = await firManager.updateGroup(groupBeingCreated!)
                    groupBeingCreated = (firManager.currentUser?.groups as [SQGroup]).first(where: { $0.id == groupBeingCreated!.id }) as SQGroup?
                    loading = false
                    showingCreatedView = true
                }
            }
        }, content: {
            CreateGroupView(groupBeingCreated: $groupBeingCreated)
        }).fullScreenCover(isPresented: $showGroupConnectedView, content: {
            GroupConnectedView()
        }).onChange(of: firManager.connectedToGroup) { oldValue, newValue in
            logger.log(level: .debug, "connectedToGroup: \(newValue)")
            showGroupConnectedView = newValue
        }.sheet(isPresented: $showingSettingsSheet, content: {
            DevSettings()
        })
    }
}

struct HomeGroupCell: View {
    var group: SQGroup
    @Environment(FIRManager.self) var firManager
    var body: some View {
        HStack {
            VStack(alignment: .leading) {
                Text(group.name).font(.title2).fontWeight(.semibold)
                Text("\(group.members.count) Members ∙ \(group.connectedMembers.count) right now").foregroundStyle(.secondary)
                Text("Last used \(lastConnectedString()) ∙ \(group.publicGroup ? "Public Group" : "Private Group")").foregroundStyle(.secondary)
            }
            Spacer()
//            if let currentSong = group.currentlyPlaying {
//                AsyncImage(url: currentSong.albumArt) { img in
//                    img.resizable().frame(width: 50, height: 50).cornerRadius(5.0)
//                } placeholder: {
//                    Image("mediaItemPlaceholder", bundle: .module, label: Text("Placeholder Cover Art")).resizable().frame(width: 50, height: 50).cornerRadius(5.0)
//                }

//            } else {
                RoundedRectangle(cornerRadius: 5.0).frame(width: 50, height: 50).opacity(0).overlay {
                    RoundedRectangle(cornerRadius: 5.0).stroke(Color.gray, lineWidth: 1.0)
                }
//            }
        }
    }

    func lastConnectedString() -> String {
        if let currentUser = firManager.currentUser {
            var lastConnectedDate = group.members.first(where: { $0.user.id == currentUser.id })?.lastConnected
            lastConnectedDate = Date().addingTimeInterval(-60 * 60 * 24 * 12)
            if let lastConnectedDate = lastConnectedDate {
                // placeholder V
                return "Recently"
            } else {
                return "Never"
            }
        } else {
            return "Loading..."
        }
    }
}
#if SKIP
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
import android.content.Intent
#endif

struct DevSettings: View {
    @Environment(FIRManager.self) var firManager
    @State var env: ServerID = .beta
    @State var serverVersion: String?
    @State var gotResult = false
    var body: some View {
        NavigationStack {
            VStack {
                List {
                    Picker("Server env", selection: $env) {
                        Text("Dev").tag(ServerID.superDev)
                        Text("\"prod\"").tag(ServerID.beta)
                    }
                    if let serverVersion = serverVersion {
                        HStack {
                            Text("Server Version")
                            Spacer()
                            Text(serverVersion).foregroundStyle(.secondary)
                        }
                    } else {
                        HStack {
                            Text("Server Version")
                            Spacer()
                            ProgressView()
                        }
                    }
                    #if SKIP
                    
                    ComposeView { ctx in
                        Button(action: {
                            var appleResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
                                    ActivityResultContracts.StartActivityForResult()
                                ) { _ in
                                    gotResult = true
                                } as ActivityResultLauncher<Intent>
                        }, label: {
                            Text("Open AM Auth")
                        }).Compose(ctx)
                    }
                    Text("\(gotResult)")
                    #endif
                }.navigationTitle("Dev Settings").frame(maxHeight: .infinity)
                
            }.onChange(of: env) { oldValue, newValue in
                firManager.env = newValue
                firManager.baseURL = "http://\(firManager.env.rawValue)"
                firManager.baseWSURL = "ws://\(firManager.env.rawValue)"
                firManager.syncManager.serverURL = URL(string: firManager.baseURL)!
                firManager.syncManager.websocketURL = URL(string: firManager.baseWSURL)!
                print(firManager.baseURL)
                Task {
                    self.serverVersion = await fetchServerVersion()
                    firManager.refreshData()
                }
            }.onAppear(perform: {
                env = firManager.env
                Task {
                    self.serverVersion = await fetchServerVersion()
                }
            })
        }
    }
    func fetchServerVersion() async -> String? {
        do {
            let (data, _) = try await URLSession.shared.data(from: URL(string: "\(firManager.baseURL)/server-version")!)
            return String(data: data, encoding: .utf8)
        } catch {
            print("error getting server info: \(error)")
        }
        return nil
    }
}

#Preview {
    ContentView()
}

struct CreateGroupView: View {
    @Environment(FIRManager.self) var firManager
    @State var groupName = ""
    @State var publicGroup = false
    @State var membersControlPlayback = true
    @State var membersAddToQueue = true
    @State var askToJoin = true
    @Environment(\.dismiss) var dismiss
    @Binding var groupBeingCreated: SQGroup?
    @State var loading = false
    var body: some View {
        ScrollView {
            VStack {
                HStack {
                    Spacer()
                    VStack {
                        Text("Create a Group").font(.title).bold()
                        Text("a group is a shared queue. you can make any type of group you want! anywhere from a massive, public group with hundreds of people at a time to a smaller, private group with a couple friends or some family. you can customize it however you’d like!").font(.subheadline).padding(.horizontal)
                    }
                    Spacer()
                }
                TextField("group name", text: $groupName).padding(.vertical)
                VStack {
                    Toggle(isOn: $publicGroup, label: {
                        VStack(alignment: .leading) {
                            Text("Public Group").font(.title2).fontWeight(.semibold)
                        }
                    })
                    Text("In a public group, the group is visible to anyone and can be joined by anyone. Best for large communities").font(.caption).foregroundStyle(.secondary)
                }.padding()
                Text("Default Permissions").font(.title2).fontWeight(.semibold)
                Toggle(isOn: $membersControlPlayback, label: {
                    Text("Members can control playback").fontWeight(.medium)
                }).padding()
                Toggle(isOn: $membersAddToQueue, label: {
                    Text("Members can add to queue").fontWeight(.medium)
                }).padding()
                VStack {
                    Toggle(isOn: $askToJoin, label: {
                        VStack(alignment: .leading) {
                            Text("Ask to Join").fontWeight(.medium)
                        }
                    })
                    Text("With Ask to Join, your permission is needed before anyone can join. Best for small groups. (not functional in the beta)").font(.caption).foregroundStyle(.secondary)
                }.padding()
                Spacer()

                HStack {
                    Spacer()
                    VStack {
                        Button {
                            Task {
                                await createGroup()
                            }
                        } label: {
                            Text("Create Group")
                        }.buttonStyle(.borderedProminent).frame(height: 50).opacity(loading ? 0.5 : 1.0).overlay {
                            if loading {
                                ProgressView()
                            }
                        }
                        Text("these options can be changed at any time via Admin Settings.").font(.caption2).foregroundStyle(.secondary)
                    }
                    Spacer()
                }
            }.padding().disabled(loading)
        }
    }

    func createGroup() async {
        loading = true
        if let currentUser = firManager.currentUser, !groupName.isEmpty {
            let group = SQGroup(id: UUID(), name: groupName, defaultPermissions: SQDefaultPermissions(id: UUID(), membersCanControlPlayback: membersControlPlayback, membersCanAddToQueue: membersAddToQueue), members: [SQGroupMember(id: UUID(), user: currentUser, canControlPlayback: true, canAddToQueue: true, isOwner: true)], publicGroup: publicGroup, askToJoin: askToJoin, previewQueue: [])
            let res = await firManager.createGroup(group)
            if res {
                loading = false
                groupBeingCreated = group
                dismiss()
            } else {
                loading = false
            }
        } else {
            logger.log(level: .debug, "she's fucked lads its so over \(groupName) \(firManager.currentUser?.username ?? "no user")")
            loading = false
        }
    }
}

