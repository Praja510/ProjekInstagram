package com.mprajadinata.projekinstagram.model

class Post {

    private var postid = ""
    private var postimage = ""
    private var publisher = ""
    private var description = ""

    constructor()
    constructor(postid: String, postimage: String, publisher: String, description: String) {
        this.postid = postid
        this.postimage = postimage
        this.publisher = publisher
        this.description = description
    }

    fun getPostId(): String {
        return postid
    }

    fun getPostImage(): String {
        return postimage
    }

    fun getPublisher(): String {
        return publisher
    }

    fun getDescription(): String {
        return description
    }

    fun setPostId(postid: String) {
        this.postid = postid
    }

    fun setPostImage(postimage: String) {
        this.postimage = postimage
    }

    fun setPublisher(publisher: String) {
        this.publisher = publisher
    }

    fun setDescription(description: String) {
        this.description = description
    }
}