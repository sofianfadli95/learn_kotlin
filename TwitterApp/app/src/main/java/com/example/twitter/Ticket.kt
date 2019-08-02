package com.example.twitter

class Ticket {
    var TweetID: String? = null
    var TweetText: String? = null
    var TweetImageURL: String? = null
    var TweetPersonUID: String? = null

    constructor(TweetID: String, TweetText: String, TweetImageURL: String, TweetPersonUID: String) {
        this.TweetID = TweetID
        this.TweetText = TweetText
        this.TweetImageURL = TweetImageURL
        this.TweetPersonUID = TweetPersonUID
    }
}