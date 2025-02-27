//
//  ContentView.swift
//  iosApp
//
//  Created by Gabriel Xavier da Silvs on 20/02/25.
//

import SwiftUI
import DemoApp


struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView().ignoresSafeArea(.keyboard)
       }
}

#Preview {
    ContentView()
}
