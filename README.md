# Brewery finder
### Niklas Raesalmi
Qt Quick application, uses QML + xml for frontend with Kotlin for backend

### Final Demo video
Brewery Finder running on an Android emulator in Android Studio using Sdk 34

[![DEMO2](https://img.youtube.com/vi/6cFK4ZzCZb4/0.jpg)](https://www.youtube.com/watch?v=6cFK4ZzCZb4)

Brewery Finder running on Oneplus Nord 2 device with Sdk 33

[![DEMO2](https://img.youtube.com/vi/N9pCGLkHnDo/0.jpg)](https://www.youtube.com/watch?v=N9pCGLkHnDo)


## Requirements
- Open source license

- Qt 6.8 (latest that you can get, likely 6.8.1)

- Android Studio, applicable version of Qt Tools for Android Studio plugin

- Desktop host

- Android emulator for API 34 Android OS 14

- Application gets Brewery information from Ireland and shows what and where is

    - the northern most brewery

    - the southern most brewery

    - and the one with the longest name

- Kotlin code handles the fetching of the data i.e controller/back-end part

- Application updates data from Kotlin code to Qt Quick QML, QML presents the data

- Part of the UI is done with Kotlin, part with QML

## Background Information

I have previously used Kotlin and Jetpack Compose for Android app development, and had to learn how to use QtQuick and also xml development for the project. 
I started out by first learning the Qt environment and used Qt Academy to learn QtQuick and QML. I also got interested in using Qt Creator so I studied it too using courses:
- Getting Started with Qt Creator (https://academy.qt.io/enrollments/253777491/details)
- Introduction to Qt Quick (https://academy.qt.io/enrollments/253834305/details)
- Introduction to QML (https://academy.qt.io/enrollments/253836003/details)

These courses gave me a basic understanding of working with QtQuick and QML.

Next, I started researching Qt Quick and its integration with Android Studio. I read the tutorial https://doc.qt.io/qt-6/qtquick-for-android.html on how to integrate the Qt Quick tool into the IDE.

After that I went to https://www.openbrewerydb.org/documentation to study how the API works. To fetch the correct pubs using curl I used the calls: 
- Most northern pub in Ireland using `by_dist`: `curl -X GET "https://api.openbrewerydb.org/v1/breweries?by_dist=55.380920,-7.373415&per_page=1"` 
- Most southern pub in Ireland using `by_dist`: `curl -X GET "https://api.openbrewerydb.org/v1/breweries?by_dist=51.461818,-9.417598&per_page=1"` is used.
- Pub with the longest name cannot be fetched with a single API call, as `curl -X GET https://api.openbrewerydb.org/v1/breweries/meta?by_country=ireland` returns `{"total":"70","page":"1","per_page":"50"}`, so two calls need to be made:
    - `curl -X GET "https://api.openbrewerydb.org/v1/breweries?by_country=Ireland&per_page=50&page=1"`
    - `curl -X GET "https://api.openbrewerydb.org/v1/breweries?by_country=Ireland&per_page=50&page=2"`

After finding the correct API calls, I used a Qt Quick example application qtquickview_kotlin and studied it's mainActivity and Main.qml structure. At first it seemed confusing but as I started developing it, everything clicked into place quickly.

## Creation Process

### Learning xml and API use

First, I wanted to learn how to use Kotlin with xml frontend format, so I ended up following a tutorial video on how to use APIs in Kotlin (https://www.youtube.com/watch?v=hurcmk_4QCM&ab_channel=CodeWithCal), also I searched for tips online on how to set up an app that uses XML before diving into QML. I followed the tutorial to get the API working to receive requests for pubs and display them using xml. As I implemented the API call structure I received an error:
`android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views. Expected: main Calling: Thread-3 at android.view.ViewRootImpl.checkThrea (ViewRootImpl.java:9994) at android.view.ViewRootImpl.requestLayout(ViewRootImpl.java:2082)`
I found a solution from https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi, I had to use `runOnUiThread{}` when updating UI elements. To receive the API calls correctly I needed to convert updateUI's parameters to accept a list instead of an Object, as openbrewerydb API returns a list. After this I got the Tutorial implementation working with xml to return just the Northest Pub in Ireland.

To get longest named pub, I found from https://kotlinlang.org/docs/collection-write.html#adding-elements how to append elements of a list to another list so I'd have a list of all the pubs in Ireland. To get the longest named pub I decided the best way is to loop through all the Request elements in the list, compare the lengths of their name elements, and swap the pub if a longer name was found. 

### Learning QML

Next I begun looking at how to get the QML frontend working. I started out by watching the tutorial video from https://doc.qt.io/qttoolsforandroid/ to learn how to integrate QtQuick into an Android Studio project. Then I integrated the lines used in the qtquickview_kotlin example inside its onCreate() into my own project with the help of https://doc.qt.io/qt-6/qml-in-android-studio-projects-example.html and translated the frontend xml contents into Main.qml. I got the text elements working but initially had trouble with editing the text elements. By studying the example more I found that I could use string properties to display and edit the API data. After I converted the xml binding edits into setProperty() calls I got an error:

`No interface with className org/qtproject/qt/android/QtAccessibilityInterface has been registered.Cannot set property northernPubName because QtQuickView is not loaded or ready yet.`

As the property elements didn't load fast enough before the Kotlin onCreate() tried accessing them, I tried adding `Component.onCompleted:` into Main.qml from https://doc.qt.io/qt-6/qml-qtqml-component.html but still received the same error. I ended up using https://doc.qt.io/qt-6/qquickview.html `QQuickView setStatusChangeListener` and listening for the change to `QtQmlStatus.READY` before editing the QML property fields. After that I got the calls working but ran into a problem in Main.qml as some properties kept getting an odd HEX value instead of the API call. I eventually realized that I can't have the same string in a Text id and a property, and after changing the values I got all the API calls going through. 

### Adding Kotlin functionality and fine-tuning
I wanted to add a button that updates the UI elements by making new API calls, so I first added a field for xml elements into my activity_main.xml above the qmlFrame and added a button there. Then I used findViewById<Button>(R.id.updateButton).setOnClickListener(this) which I found from https://developer.android.com/develop/ui/views/components/button to make it interactable. I added the runFetchPubs() call to the onClick() function to update the data.

I added a text field that tells how many pubs are in Ireland, and to avoid making a structure object for the metadata I wanted to only get the first element of the received object. To make the API call return only one element of an object instead of the whole object I found https://kotlinlang.org/docs/collection-elements.html using .first() gets the first element of the object.

Then I added null checks for all the elements and had a little trouble with handling the address because if there were null fields they still showed up, so from https://www.oreilly.com/library/view/hands-on-object-oriented-programming/9781789617726/1cf495eb-2f24-4411-8fd2-f1bd0f74a70c.xhtml I found that using listOfNotNull() works well here but kept leaving just the commas if the fields were null. I found from https://stackoverflow.com/questions/53007055/string-append-with-null-check-kotlin that using listOfNotNull().takeIf { it.isNotEmpty() }?.joinToString(", ") solved the issue.

## What I learned

Learning xml and especially QML was fun and the experience was challenging but very rewarding, as I got to learn much about new tools and techniques for Android app development. I also enjoyed learning more about frontend and backend interaction and how easily QML can be integrated into a Kotlin application. Centric and important topics I learned during the development: 

- Basic XML UI development before switching to QML.
- Using Qt Quick & QML for UI development.
- Integrating Qt Quick with Android Studio with Qt Tools.
- Binding Kotlin backend to QML 
- Making API requests and parsing JSON with Gson.
- Ensuring how QML elements can be prevented from accessing before they are ready
