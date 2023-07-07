package com.example.mymusicplayer.other

data class Resource<out T>(val status:Status,val data:T?, val massage: T?) {
    companion object{
        fun <T> success(data:T?)=Resource(Status.SUCCESS,data,null)


        fun <T> error(massage:String,data: T?)=Resource(Status.ERROR,data,massage)


        fun <T> loading(data:T?)=Resource(Status.LOADING,data,null)
    }
}
enum class Status{
    SUCCESS,
    ERROR,
    LOADING
}