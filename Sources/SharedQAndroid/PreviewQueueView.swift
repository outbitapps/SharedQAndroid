//
//  SwiftUIView.swift
//  
//
//  Created by Payton Curry on 6/2/24.
//

import SwiftUI
import SharedQProtocol

struct PreviewQueueView: View {
    var group: SQGroup
    var body: some View {
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
            BaseQueueView(group: group).frame(maxHeight: .infinity)
        }
    }
}
