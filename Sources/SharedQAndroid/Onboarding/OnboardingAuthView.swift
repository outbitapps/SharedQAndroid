//
//  SwiftUIView.swift
//  
//
//  Created by Payton Curry on 5/28/24.
//

//import SkipUI
import SwiftUI
import SharedQProtocol
import Observation

enum CurrentAuthView {
    case none
    case signup
    case login
}

struct OnboardingAuthView: View {
    @AppStorage("accountCreated") var accountCreated = false
    @State var navPath = [String]()
    @State var currentView: CurrentAuthView = .none
    var body: some View {
        NavigationStack(path: $navPath) {
            ZStack {
                VStack {
                    Text(verbatim: "Welcome to SharedQ").font(.system(.largeTitle)).fontWeight(.bold)
                    
                    Text("create a shared music queue no matter what streaming service you use").multilineTextAlignment(.center)
                    Spacer()
                    ZStack {
                        switch currentView {
                        case .none:
                            ZStack {
                                RoundedRectangle(cornerRadius: 20.0).foregroundStyle(.black)
                                GeometryReader(content: { geometry in
                                    HStack {
                                        ZStack {
                                            Image(systemName: "envelope.fill").foregroundStyle(.white)
                                        }.frame(width: geometry.size.width / 6)
                                        Button(action: {
                                            withAnimation(.spring) {
                                                currentView = .signup
                                            }
                                        }, label: {
                                            ZStack {
                                                RoundedRectangle(cornerRadius: 10.0).foregroundStyle(SharedQAssets.buttonDark)
                                                Text("Sign Up").foregroundStyle(.white)
                                            }
                                        })
                                        Button(action: {
                                            withAnimation(.spring) {
                                                currentView = .login
                                            }
                                        }, label: {
                                            ZStack {
                                                RoundedRectangle(cornerRadius: 10.0).foregroundStyle(SharedQAssets.buttonDark)
                                                Text("Log In").foregroundStyle(.white)
                                            }
                                        })
                                    }.padding(10)
                                })
                            }.frame(height: 70)
                        case .signup:
                            SignupView {
                                navPath.append("music-services")
                            }.transition(.scale).frame(height: 500)
                        case .login:
                            LoginView {
                                navPath.append("music-services")
                            }.transition(.scale).frame(height: 350)
                        }
                    }
                    
                }.padding()
            }.navigationDestination(for: String.self) { path in
                switch path {
                case "music-services":
                    OnboardingMusicServices(navPath: $navPath).navigationBarBackButtonHidden()
                case "final-notes":
                    OnboardingFinalNotes()
                default:
                    Text("It's fucked, sorry!")
                }
            }.onAppear {
                if accountCreated {
                    navPath.append("music-services")
                }
            }
        }
    }
}
struct SharedQAssets {
    static var appGradient1 = Color(red: 0.910, green: 0.180, blue: 0.180)
    static var appGradient2 = Color(red: 0.922, green: 0.384, blue: 0.157)
    static var buttonDark = Color(red: 0.1, green: 0.1, blue: 0.1)
}

struct SignupView: View {
    @Environment(FIRManager.self) var firManager
    @AppStorage("accountCreated") var accountCreated = false
    @State var username = ""
    @State var email = ""
    @State var password = ""
    @State var error: String?
    @State var loading = false
    var onSuccess: () -> Void
    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 35.0).foregroundStyle(.black)
            VStack {
                if let error {
                    Text(error).foregroundStyle(.red)
                }
                VStack(alignment: .leading) {
                    Text("username").font(.callout).foregroundStyle(.white)
                    TextField("username...", text: $username).textFieldStyle(.plain).roundedBorder(15.0, color: Color.white, width: 1.0).padding(.bottom)
                }
                VStack(alignment: .leading, content: {
                    Text("email").font(.callout).foregroundStyle(.white)
                    TextField("email...", text: $email).textFieldStyle(.plain).roundedBorder(15.0, color: Color.white, width: 1.0).padding(.bottom)
                })
                VStack(alignment: .leading, content: {
                    Text("password").font(.callout).foregroundStyle(.white)
                    SecureField("password...", text: $password).textFieldStyle(.plain).roundedBorder(15.0, color: Color.white, width: 1.0).padding(.bottom)
                })
                Spacer()
                Button(action: {
                    if !loading {
                        loading = true
                        Task {
                            let res = await firManager.signUp(username:username, email:email, password:password)
                            loading = false
                            if res == .success {
                                accountCreated = true
                                onSuccess()
                            } else {
                                error = res.rawValue
                            }
                        }
                    }
                }, label: {
                    ZStack {
                        ZStack {
                            RoundedRectangle(cornerRadius: 15.0).foregroundStyle(SharedQAssets.buttonDark)
                            Text("Sign Up").foregroundStyle(.white)
                        }.opacity(loading ? 0.5 : 1.0)
                        if loading {
                            ProgressView()
                        }
                    }
                }).frame(height: 60).disabled(loading)
            }.padding(20)
        }
    }
}

struct LoginView: View {
    @Environment(FIRManager.self) var firManager
    @AppStorage("accountCreated") var accountCreated = false
    @State var email = ""
    @State var password = ""
    @State var error: String?
    @State var loading = false
    var onSuccess: () -> Void
    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 35.0).foregroundStyle(.black)
            VStack {
                if let error {
                    Text(error).foregroundStyle(.red)
                }
                VStack(alignment: .leading, content: {
                    Text("email").font(.callout).foregroundStyle(.white)
                    TextField("email...", text: $email).textFieldStyle(.plain).roundedBorder(15.0, color: Color.white, width: 1.0).padding(.bottom)
                })
                VStack(alignment: .leading, content: {
                    Text("password").font(.callout).foregroundStyle(.white)
                    SecureField("password...", text: $password).textFieldStyle(.plain).roundedBorder(15.0, color: Color.white, width: 1.0).padding(.bottom)
                })
                Spacer()
                Button(action: {
                    if !loading {
                        loading = true
                        Task {
                            let res = await firManager.signIn(email:email, password:password)
                            loading = false
                            if res == .success {
                                accountCreated = true
                                onSuccess()
                            } else {
                                error = res.rawValue
                            }
                        }
                    }
                }, label: {
                    ZStack {
                        ZStack {
                            RoundedRectangle(cornerRadius: 15.0).foregroundStyle(SharedQAssets.buttonDark)
                            Text("Log In").foregroundStyle(.white)
                        }.opacity(loading ? 0.5 : 1.0)
                        if loading {
                            ProgressView()
                        }
                    }
                }).frame(height: 60).disabled(loading)
            }.padding(20)
        }
    }
}

extension View {
    @ViewBuilder
    func roundedBorder(_ radius: CGFloat = 10.0, color: Color = .white, width: CGFloat = 1.0) -> some View {
        return self.overlay(content: {
            RoundedRectangle(cornerRadius: radius).stroke(color, lineWidth: width)
        })
    }
}
