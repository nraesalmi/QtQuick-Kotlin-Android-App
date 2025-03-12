// Copyright (C) 2024 The Qt Company Ltd.
// SPDX-License-Identifier: LicenseRef-Qt-Commercial OR GPL-3.0-only WITH Qt-GPL-exception-1.0

import QtQuick

Rectangle {
    width: parent.width
    height: parent.height
    visible: true
    color: "white"

    Text {
        id: helloText
        color: "white"
        text: qsTr("")
        anchors.centerIn: parent
    }
}

