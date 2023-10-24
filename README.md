# Popular Movies

### Purpose
* Template for a modular MVVM architecture -- therefore a very limited feature set

### Technologies 
* Uses the [Open Movie database] (https://api.themoviedb.org/) to fetch data. The Api-Key ist part of the repository so that the project would compile out of the box (this is of course not a real life praxis)
* Scalable and modular architecture based on MVVM -- inspired by the open source [Now In Android] (https://github.com/android/nowinandroid) App.
* Kotlin, Coroutines, Hilt, Compose, Coil, Gradle build system with the new Version Catalog feature.

### This is a work in progress -- next steps:
* Test coverage throughout the layers
* Infinite scrolling by using the api's pagination
* Bookmarking feature + NavigationRail
* Offline first
* Settings -- e.g. Items order, App Theme ...