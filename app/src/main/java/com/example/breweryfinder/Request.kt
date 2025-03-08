package com.example.breweryfinder

data class Request(
    var name: String,
    var brewery_type: String,
    var address_1: String,
    var address_2: String,
    var address_3: String,
    var longitude: String,
    var latitude: String,
    var phone: String,
    var website_url: String
)
