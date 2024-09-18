//
//  FIRManager.swift
//  SharedQueue
//
//  Created by Payton Curry on 3/24/24.
//

import Foundation
import SharedQSync
import SharedQProtocol
import Observation
import OSLog

@Observable
class FIRManager {
    var currentUser: SQUser?
    var connectedGroup: SQGroup?
    var connectedToGroup = false
    var loaded = false
    var authToken: String?
    var syncManager: SharedQSyncManager
    var setupQueue = false
    var env: ServerID
    var baseURL: String
    var baseWSURL: String
    init(env: ServerID = ServerID.superDev) {
        logger.debug("construcyor")
        self.env = env
        baseURL = "http://\(env.rawValue)"
        baseWSURL = "ws://\(env.rawValue)"
        syncManager = SharedQSyncManager(serverURL: URL(string: "http://\(env.rawValue)")!, websocketURL: URL(string: "ws://\(env.rawValue)")!)
        syncManager.delegate = self
            authToken = UserDefaults.standard.string(forKey: "auth_token")
            self.refreshData()
    }

    func refreshData() {
        self.loaded = false
        if let authToken = authToken {
            
            var userRequest = URLRequest(url: URL(string: "\(baseURL)/users/fetch-user")!)
            userRequest.httpMethod = "GET"
            userRequest.setValue("Bearer \(authToken)", forHTTPHeaderField: "Authorization")
            logger.log(level: .debug, "about to make request")
            do {
                Task { [userRequest] in
                    let (data, res) = try await URLSession.shared.data(for: userRequest)
                    if let user = try? JSONDecoder().decode(SQUser.self, from: data) {
                        DispatchQueue.main.async {
                            self.currentUser = user
                            logger.debug("user: \(user.username)")
                            self.loaded = true
                        }
                    } else {
                        print(String(data: data, encoding: .utf8))
                        logger.error("status code: \(res.http?.statusCode ?? 0)")
                        if let http = res.http, http.statusCode == 401 {
                            UserDefaults.standard.set(false, forKey: "accountCreated")
                            UserDefaults.standard.set(false, forKey: "accountSetup")
                            UserDefaults.standard.set(true, forKey: "needsOnboarding")
                        }
                    }
                }
            } catch {
                logger.error("error getting user: \(error)")
            }
        } else {
            logger.error("no auth token")
        }
    }
    
    func pauseSong() async {
        await musicService.pauseSong()
        try? await self.syncManager.pauseSong()
        self.connectedGroup?.playbackState?.state = .pause
    }
    
    func playSong() async {
        if let connectedGroup = connectedGroup, let currentlyPlaying = connectedGroup.currentlyPlaying {
            await musicService.playSong(song: currentlyPlaying)
            try? await self.syncManager.playSong()
            self.connectedGroup?.playbackState?.state = .play
        }
    }
    
    func nextSong() async {
        await musicService.nextSong()
        try? await self.syncManager.nextSong()
    }

    func createGroup(_ group: SQGroup) async -> Bool {
        logger.debug("createGroup: \(group.name)")
        var userRequest = URLRequest(url: URL(string: "\(baseURL)/groups/create")!)
        userRequest.httpMethod = "POST"
        userRequest.setValue("Bearer \(authToken ?? "unauth'd")", forHTTPHeaderField: "Authorization")
        print(userRequest.allHTTPHeaderFields)
        userRequest.httpBody = try! JSONEncoder().encode(group)
        do {
            let (data, res) = try await URLSession.shared.data(for: userRequest)
            if let http = res.http {
                await self.refreshData()
                logger.debug("createGroup: \(http.statusCode)")
                let range =  200...299
                return range.contains(http.statusCode)
            }
            
        } catch {
            logger.error("error w/ createGroup: \(error)")
        }
        return false
    }
    
    func signUp(username: String, email: String, password: String) async -> SQSignUpResponse {
        var userRequest = URLRequest(url: URL(string: "\(baseURL)/users/signup")!)
        userRequest.httpMethod = "POST"
        userRequest.httpBody = try? JSONEncoder().encode(UserSignup(email: email, username: username, password: password))
        do {
            let (data, res) = try await URLSession.shared.data(for: userRequest)
//            if String(data: data, encoding: .utf8) == "Success!" || String(data: data, encoding: .utf8) == "User already exists!" {
//                return true
//            } else {
//                print("respose from create: \(String(data: data, encoding: .utf8))")
//            }
            if let tokenResponse = try? JSONDecoder().decode(NewSession.self, from: data) {
                print(tokenResponse.token)
                UserDefaults.standard.set(tokenResponse.token, forKey: "auth_token")
                DispatchQueue.main.async {
                    self.currentUser = tokenResponse.user
                    self.authToken = tokenResponse.token
                }
                return SQSignUpResponse.success
            } else {
                if let http = res.http {
                    logger.log(level: .debug, "\(http.statusCode)")
                    if http.statusCode == 409 {
                        return .alreadyExists
                    }
                    if http.statusCode == 406 {
                        return .badValue
                    }
                }
            }
        } catch {
            print(error)
        }
        return SQSignUpResponse.noConnection
    }
    
    func signIn(email: String, password: String) async -> SQSignUpResponse {
        var userRequest = URLRequest(url: URL(string: "\(baseURL)/users/login")!)
        userRequest.httpMethod = "PUT"
//        userRequest.httpBody = try? JSONEncoder().encode(UserSignup(email: email, username: username, password: password))
        userRequest.addValue("Basic \("\(email.lowercased()):\(password)".data(using: .utf8)?.base64EncodedString() ?? "asf")", forHTTPHeaderField: "Authorization")
        //fuck you Swift
        userRequest.httpBody = Data()
        do {
            let (data, res) = try await URLSession.shared.data(for: userRequest)
            if let tokenResponse = try? JSONDecoder().decode(NewSession.self, from: data) {
                print(tokenResponse.token)
                UserDefaults.standard.set(tokenResponse.token, forKey: "auth_token")
                DispatchQueue.main.async {
                    self.currentUser = tokenResponse.user
                    self.authToken = tokenResponse.token
                }
                return SQSignUpResponse.success
            } else {
                if let http = res.http {
                    print(http.statusCode)
                    if http.statusCode == 401 {
                        return .incorrectPassword
                    }
                }
            }
        } catch {
            logger.error("error logging in: \(error)")
        }
        return SQSignUpResponse.noConnection

    }
    
    func sendPasswordResetEmail(email: String) async {
        do {
            var request = URLRequest(url: URL(string: "\(baseURL)/users/pwresetemail/\(email)")!)
            request.httpMethod = "POST"
            let (data, res) = try await URLSession.shared.data(for: request)
            if let http = res.http {
                print(http.statusCode)
            }
        } catch {
            print(error)
        }
    }

    func updateGroup(_ group: SQGroup) async -> Bool {
        var userRequest = URLRequest(url: URL(string: "\(baseURL)/groups/update")!)
        userRequest.httpMethod = "PUT"
        userRequest.httpBody = try! JSONEncoder().encode(group)
        userRequest.setValue("Bearer \(authToken ?? "unauth'd")", forHTTPHeaderField: "Authorization")
        print("sending \(userRequest.httpBody) to server")
        do {
            let (data, res) = try await URLSession.shared.data(for: userRequest)
//            if let http = res.http {
//                if 200...299 ~= http.statusCode {
//                 await refreshData()
//                    return true
//                }
//            }
        } catch {
            print(error)
        }
        return false
    }

    func addGroup(_ groupID: String, _ groupURLID: String) async -> Bool {
        var userRequest = URLRequest(url: URL(string: "\(baseURL)/groups/add-group/\(groupID)/\(groupURLID)")!)
        print(userRequest.url)
        if currentUser == nil {
            await refreshData()
        }
        userRequest.setValue("Bearer \(authToken ?? "unauth'd")", forHTTPHeaderField: "Authorization")
        userRequest.httpMethod = "PUT"
        userRequest.httpBody = try! JSONEncoder().encode(AddGroupRequest(myUID: currentUser!.id))
        do {
            let (data, res) = try await URLSession.shared.data(for: userRequest)
            print(String(data: data, encoding: .utf8))
//            if let http = res.http {
//                await refreshData()
//                return 200...299 ~= http.statusCode
//            }
        } catch {
            print(error)
        }
        return false
    }
}

var musicService = BasicMusicService()

extension FIRManager: SharedQSyncDelegate {
    func onDisconnect() {
        Task {
            await musicService.stopPlayback()
        }
        connectedToGroup = false
    }

    func onGroupConnect(_ group: SQGroup) {
        logger.log(level: OSLogType.debug, "connected to \(group.name)")
        connectedToGroup = true
        self.connectedGroup = group
        Task {
            await musicService.playSong(song: connectedGroup!.currentlyPlaying!)
        }
    }

    func onGroupUpdate(_ group: SQGroup, _ message: WSMessage) {
        print("group update")
//        self.connectedGroup = group
        DispatchQueue.main.async {
            self.connectedGroup = group
        }
        if group.playbackState!.state == .pause {
            Task {
                await musicService.pauseSong()
            }
        }
        Task {
            var queue = [SQSong]()
            for item in group.previewQueue {
                queue.append(item.song)
            }
            await musicService.addQueue(queue: queue)
        }
    }

    func onNextSong(_ message: WSMessage) {
        Task {
            await musicService.playSong(song: connectedGroup!.currentlyPlaying!)
            var delay = Date().timeIntervalSince(message.sentAt)
            await musicService.playAt(timestamp: delay)
        }
    }

    func onPrevSong(_ message: WSMessage) {
        Task {
            await musicService.prevSong()
        }
    }

    func onPlay(_ message: WSMessage) {
        Task {
            await musicService.playSong(song: connectedGroup!.currentlyPlaying!)
            await musicService.playAt(timestamp: connectedGroup!.playbackState!.timestamp)
        }
        self.connectedGroup!.playbackState?.state = .play
    }

    func onPause(_ message: WSMessage) {
        print("paused at \(message.sentAt)")
        Task {
            await musicService.pauseSong()
        }
        self.connectedGroup!.playbackState?.state = .pause
    }

    func onTimestampUpdate(_ timestamp: TimeInterval, _ message: WSMessage) {
        Task {
            var delay = Date().timeIntervalSince(message.sentAt)
            print(delay)
            var timestampDelay = await musicService.getSongTimestamp() - (timestamp + delay)
            print(timestampDelay)
            if !(timestampDelay <= 1 && timestampDelay >= -1) {
                print(timestamp)
                await musicService.playAt(timestamp: timestamp + delay)
            }
        }
    }

    func onSeekTo(_ timestamp: TimeInterval, _ message: WSMessage) {
        Task {
            await musicService.seekTo(timestamp: timestamp)
        }
    }
}

enum ServerID: String {
    case superDev = "192.168.68.112:8080"
    case beta = "sq.paytondev.cloud:8080"
}

//extension Data {
//    var bytes: [UInt8] {
//        return [UInt8](self)
//    }
//}

//extension Array where Element == UInt8 {
//    var data: Data {
//        return Data(self)
//    }
//}

extension URLResponse {
    /// Returns casted `HTTPURLResponse`
    var http: HTTPURLResponse? {
        return self as? HTTPURLResponse
    }
}

enum SQSignUpResponse: String {
    case success = "Success!"
    case alreadyExists = "That account alredy exists! Try logging in instead."
    case noConnection = "Unable to reach SharedQ. Maybe check your network connection?"
    case incorrectPassword = "That password wasn't correct. Try again!"
    case badValue = "Either your username, email, or password was invalid. Try again!"
}
