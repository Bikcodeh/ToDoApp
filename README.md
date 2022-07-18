[![kotlin](https://img.shields.io/github/languages/top/bikcodeh/ToDoApp.svg?style=for-the-badge&color=blueviolet)](https://kotlinlang.org/) [![Android API](https://img.shields.io/badge/api-26%2B-brightgreen.svg?style=for-the-badge)](https://android-arsenal.com/api?level=26)

# Notes App  

## :star: Features

- [x] Create notes
- [x] Update notes
- [x] Delete individual notes or all
- [x] Search a particular note searching by title or description
- [x] Sort notes by priority

:runner: For run the app just clone the repository and execute the app on Android Studio.

### Requirements to install the app
- Use phones with Android Api 26+

##### This application was developed using Kotlin and uses the following components:
- Coroutines
- Clean architecture (Domain, Data, UI)
- MVVM
- Repository pattern
- StateFlow
- Material Design 3
- Data store
- Navigation component
- Dagger Hilt (Dependency injection)
- Room database
- Flow

## Screenshots Light theme
 | Home Empty |     Home    |  Add  |   Update    |
 | :----: | :---------: | :-------: | :-----------: |
 |![Home empty](assets/home.png?raw=true)|![Home](assets/home_notes.png?raw=true)|![Add](assets/add.png?raw=true)|![Update](assets/update.png?raw=true)|

## Screenshots Dark Mode
 | Home Empty |     Home    |  Add  |   Update    |
 | :----: | :---------: | :-------: | :-----------: |
 |![Home empty dark](assets/home_dark.png?raw=true)|![Home dark](assets/home_notes_dark.png?raw=true)|![Add dark](assets/add_dark.png?raw=true)|![Update dark](assets/update_dark.png?raw=true)|

## :dart: Architecture

The application is built using Clean Architeture pattern based on [Architecture Components](https://developer.android.com/jetpack/guide#recommended-app-arch) on Android. The application is divided into three layers:

![Clean Arquitecture](https://devexperto.com/wp-content/uploads/2018/10/clean-architecture-own-layers.png)

- Domain: This layer contains the business logic of the application, here we define the data models and the use cases.
- Data: This layer contains the data layer of the application. It contains the database, network and the repository implementation.
- UI: This layer contains the presentation layer of the application like fragment, activity, viewmodel etc.

## License

MIT

**Bikcodeh**
