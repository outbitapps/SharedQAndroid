//
//  SwiftUIView.swift
//  
//
//  Created by Payton Curry on 5/30/24.
//

import SwiftUI

struct OnboardingFinalNotes: View {
    @AppStorage("needsOnboarding") var needsOnboarding = true
    var body: some View {
        VStack(alignment: .leading) {
            Text("you're ready to use SharedQ, a shared music queue which works regardless of what streaming service you use. but first, here's how SharedQ works:").font(.caption)
            HowItWorksCard(systemImage: "plus.circle.fill", title: "Groups", content: "Queues are organized into \"Groups\", which are different groups which you can be apart of.").padding(.vertical, 5)
            HowItWorksCard(systemImage: "play.fill", title: "Playback Controls", content: "By default, anybody in a group can control playback controls (play/pause, forward/backward, etc.)").padding(.vertical, 5)
            HowItWorksCard(systemImage: "person.crop.circle.fill", title: "Who can Join", content: "By default, groups are set to private which means that only the group's creator can invite people. Public groups are discoverable and joinable by anybody, and are best suited for large communities.").padding(.vertical, 5)
            HowItWorksCard(systemImage: "bell.fill", title: "Audio Playback", content: "As soon as you join a group, the audio from that group **immediately** starts playing. Don't be startled, it's okay! Audio plays on everybody's devices.").padding(.vertical, 5)
            HStack {
                Spacer()
                Button {
                    needsOnboarding = false
                } label: {
                    HStack {
                        Text("Let's do this!")
                        Image(systemName: "heart.fill")
                    }
                        
                }.frame(height: 70).padding().buttonStyle(.borderedProminent)
                Spacer()
            }
            Spacer()
        }.padding().navigationTitle("Final Notes")
    }
}


struct HowItWorksCard: View {
    var systemImage: String
    var title: String
    var content: String
    var body: some View {
        HStack {
            Image(systemName: systemImage).font(.title3).frame(width: 50)
            VStack(alignment: .leading) {
                Text(title).font(.title3).bold()
                Text(content)
            }
        }.opacity(0.8)
    }
}




