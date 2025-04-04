import QtQuick

Rectangle {
    width: parent.width
    height: parent.height
    visible: true
    color: "white"

    property string loading: "Loading..."

    property string northPubName: loading
    property string northBreweryType: loading
    property string northAddress: loading
    property string northLatitude: loading
    property string northLongitude: loading
    property string northPhone: loading
    property string northWebsite: loading

    property string southPubName: loading
    property string southBreweryType: loading
    property string southAddress: loading
    property string southLatitude: loading
    property string southLongitude: loading
    property string southPhone: loading
    property string southWebsite: loading

    property string longPubName: loading
    property string longBreweryType: loading
    property string longAddress: loading
    property string longLatitude: loading
    property string longLongitude: loading
    property string longPhone: loading
    property string longWebsite: loading

    property string randomPubName: loading
    property string randomBreweryType: loading
    property string randomAddress: loading
    property string randomLatitude: loading
    property string randomLongitude: loading
    property string randomPhone: loading
    property string randomWebsite: loading

    Column {
        anchors.left: parent.left
        anchors.margins: 16
        spacing: 12

        // Most Northern Pub
        Text {
            id: northernPub
            text: "Most Northern Irish Pub:"
            color: "#292929"
            font.pixelSize: 46
            font.bold: true
        }

        Text {
            id: northernName
            text: "Name: " + northPubName
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: northernType
            text: "Brewery Type: " + northBreweryType
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: northernAddressText
            text: "Address: " + northAddress
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: northernLatitude
            text: "Latitude: " + northLatitude
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: northernLongitude
            text: "Longitude: " + northLongitude
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: northernPhone
            text: "Phone: " + northPhone
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: northernWebsite
            text: "Website Url: " + northWebsite
            color: "#292929"
            font.pixelSize: 38
        }

        // Most Southern Irish Pub
        Text {
            id: southernPub
            text: "Most Southern Irish Pub:"
            color: "#292929"
            font.pixelSize: 46
            font.bold: true
        }

        Text {
            id: southernName
            text: "Name: " + southPubName
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: southernType
            text: "Brewery Type: " + southBreweryType
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: southernAddressText
            text: "Address: " + southAddress
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: southernLatitude
            text: "Latitude: " + southLatitude
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: southernLongitude
            text: "Longitude: " + southLongitude
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: southernPhone
            text: "Phone: " + southPhone
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: southernWebsite
            text: "Website Url: " + southWebsite
            color: "#292929"
            font.pixelSize: 38
        }

        // Longest Named Pub
        Text {
            id: longestNamePub
            text: "Pub with the Longest Name in Ireland:"
            color: "#292929"
            font.pixelSize: 46
            font.bold: true
        }

        Text {
            id: longestName
            text: "Name: " + longPubName
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: longestNameType
            text: "Brewery Type: " + longBreweryType
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: longestNameAddressText
            text: "Address: " + longAddress
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: longestNameLatitude
            text: "Latitude: " + longLatitude
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: longestNameLongitude
            text: "Longitude: " + longLongitude
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: longestNamePhone
            text: "Phone: " + longPhone
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: longestNameWebsite
            text: "Website Url: " + longWebsite
            color: "#292929"
            font.pixelSize: 38
        }

        // Random Pub
        Text {
            id: randomPub
            text: "Random Pub in Ireland:"
            color: "#292929"
            font.pixelSize: 46
            font.bold: true
        }

        Text {
            id: randomName
            text: "Name: " + randomPubName
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: randomType
            text: "Brewery Type: " + randomBreweryType
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: randomAddressText
            text: "Address: " + randomAddress
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: randomLatitudeText
            text: "Latitude: " + randomLatitude
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: randomLongitudeText
            text: "Longitude: " + randomLongitude
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: randomPhoneText
            text: "Phone: " + randomPhone
            color: "#292929"
            font.pixelSize: 38
        }

        Text {
            id: randomWebsiteText
            text: "Website Url: " + randomWebsite
            color: "#292929"
            font.pixelSize: 38
        }
    }
}
