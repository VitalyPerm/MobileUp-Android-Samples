package ru.mobileup.samples.features.shared_element_transitions.data

import ru.mobileup.samples.features.image.domain.ImageUrl
import ru.mobileup.samples.features.shared_element_transitions.domain.ItemSharedElement

object ItemSharedElementItems {
    val items = listOf(
        ItemSharedElement(
            id = 1,
            title = "\uD83D\uDE80 10 Kotlin Coroutine Mistakes Every Senior Android Developer Must Avoid (With Real-World Fixes!)",
            text = "Kotlin Coroutines have revolutionized asynchronous programming on Android, making it simpler, cleaner, and more intuitive. However, even seasoned Android developers can fall into common traps that lead to memory leaks, performance bottlenecks, or unpredictable behavior in apps. In this post, we’ll explore the 10 most common coroutine mistakes that every senior developer should avoid, complete with real-world examples and practical solutions. Whether you’re optimizing app performance or ensuring robust lifecycle management, this guide will help you harness the true power of Kotlin Coroutines like a pro. \uD83D\uDCA1\n" +
                    "\n" +
                    "My article is free for everyone. If you don’t have active subscription, you can read it here. Otherwise, please continue \uD83D\uDE09",
            image = ImageUrl("https://miro.medium.com/v2/resize:fit:720/format:webp/1*pBvhnR45FvP3r_G0EkY3Wg.png"),
        ),
        ItemSharedElement(
            id = 2,
            title = "Mastering Kotlin Coroutine Channels in Android: From Basics to Advanced Patterns",
            text = "Coroutines in Kotlin have transformed asynchronous programming, particularly in Android development, by replacing complex callback patterns with structured concurrency. However, a common challenge arises when coroutines need to communicate with each other. Since coroutines can run on different threads and operate independently, establishing reliable communication channels between them requires a thoughtful approach.\n\nKotlin Channels provide a type-safe way for coroutines to communicate by passing data streams between them. They implement a producer-consumer pattern that ensures each piece of data is delivered exactly once, making them ideal for coordinated communication between concurrent operations.",
            image = ImageUrl("https://miro.medium.com/v2/resize:fit:720/format:webp/0*wQUC5c0t6OzQMQyR")
        ),
        ItemSharedElement(
            id = 3,
            title = "Modeling ViewModel State in Android: A Guide to Clean, Scalable Patterns",
            text = "Poorly designed models create a cascade of complications for every component that depends on them. In the case of presentation models, when these don’t align with the screen’s actual needs, other components (like ViewModels) are forced to work around them, resulting in bloated, hard-to-maintain classes filled with hacks and workarounds. This lack of alignment introduces ambiguity and confusion, leading to unclear, error-prone code that is costly to maintain.",
            image = ImageUrl("https://miro.medium.com/v2/resize:fit:1100/format:webp/1*3nU2fwxJNvRImqX86dj0dw.png")
        ),
        ItemSharedElement(
            id = 4,
            title = "Top 10 Coroutine Mistakes We All Have Made as Android Developers",
            text = "As Android developers, Kotlin coroutines have become an indispensable tool in our asynchronous programming toolkit. They simplify concurrent tasks, make code more readable, and help us avoid the callback hell that was prevalent with earlier approaches. However, coroutines come with their own set of challenges, and it’s easy to fall into common pitfalls that can lead to bugs, crashes, or suboptimal performance.\n" +
                    "\n" +
                    "In this article, we’ll explore the top 10 coroutine mistakes that many of us have made (often unknowingly) and provide guidance on how to avoid them. Whether you’re a seasoned developer or just starting with coroutines, this guide aims to enhance your understanding and help you write more robust asynchronous code.",
            image = ImageUrl("https://miro.medium.com/v2/resize:fit:720/format:webp/1*Bhfzsp01NibPkNYmWy9YyA.png")
        ),
        ItemSharedElement(
            id = 5,
            title = "Benchmarking Koin vs. Dagger Hilt in Modern Android Development (2024)",
            text = "When choosing a dependency injection framework for Android and Kotlin development, performance is often a key consideration. This article explores the performance of Koin in its latest version (4.0.1-Beta1) and compares it with Dagger Hilt (2.52). Rather than relying on simplistic benchmarks or limited code execution scenarios, the focus is “developer-centric”: understanding performance in real-world, day-to-day usage. Additionally, this article aims to reassure those who may hesitate to adopt Koin due to performance concerns.",
            image = ImageUrl("https://miro.medium.com/v2/resize:fit:720/format:webp/1*h5ULv-tGdDA3yD2c685lFQ.png")
        ),
        ItemSharedElement(
            id = 6,
            title = "The two best ways to secure your API keys in Android projects",
            text = "Protecting your Android app’s API keys ensures the privacy of user information and secures any unauthorized access to your APIs, this is important and every application should make sure to secure their API keys to prevent any misuse of their services.",
            image = ImageUrl("https://miro.medium.com/v2/resize:fit:4800/format:webp/1*ba3JJ-JCIWGZJi94UTnThw.png")
        )
    )
}