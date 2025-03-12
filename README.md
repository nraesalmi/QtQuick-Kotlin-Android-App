# Brewery finder
### Niklas Raesalmi
Qt Quick application, uses QML for frontend & Kotlin backend

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

## Creation Process
I started out by researching Qt Quick and its integration with Android Studio. I read the tutorial https://doc.qt.io/qt-6/qtquick-for-android.html on how to integrate the Qt Quick tool into the IDE.

Next I went to https://www.openbrewerydb.org/documentation to study how the API works. To fetch the correct pubs using curl I got the calls: 
- Most northern pub in Ireland using `by_dist`: `curl -X GET "https://api.openbrewerydb.org/v1/breweries?by_dist=55.380920,-7.373415&per_page=1"` 
- Most southern pub in Ireland using `by_dist`: `curl -X GET "https://api.openbrewerydb.org/v1/breweries?by_dist=51.461818,-9.417598&per_page=1"` is used.
- Pub with the longest name cannot be fetched with a single API call, as `curl -X GET https://api.openbrewerydb.org/v1/breweries/meta?by_country=ireland` returns `{"total":"70","page":"1","per_page":"50"}`, so two calls need to be made:
    - `curl -X GET "https://api.openbrewerydb.org/v1/breweries?by_country=Ireland&per_page=50&page=1"`
    - `curl -X GET "https://api.openbrewerydb.org/v1/breweries?by_country=Ireland&per_page=50&page=2"`

Next I found a tutorial video on how to use APIs in Kotlin, also I searched for tips online on how to set up an app that uses XML before diving into QML. I followed the tutorial https://www.youtube.com/watch?v=hurcmk_4QCM&ab_channel=CodeWithCal to get the API working to receive a request for pubs.

Then I begun looking at how to get the QML frontend working. I found a Qt Quick example application qtquickview_kotlin and studied it's mainActivity structure. I then integrated the lines inside its onCreate() into my own project and moved the xml contents into activity_main.xml and followed a similar structure that was implemented in the example application. After that I received an error:
`android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views. Expected: main Calling: Thread-3
at android.view.ViewRootImpl.checkThread(ViewRootImpl.java:9994)
at android.view.ViewRootImpl.requestLayout(ViewRootImpl.java:2082)`

and found a solution from https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi, I had to use runOnUiThread{} when updating UI elements.

After this I got the Tutorial implementation working with QML
