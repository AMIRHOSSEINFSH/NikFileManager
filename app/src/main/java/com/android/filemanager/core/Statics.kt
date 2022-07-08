package com.android.filemanager.core

const val FILESEPRATOR = "/"
const val IS_LINEAR = "is_linear"
const val IS_SELECTED = "is_linear"
const val PERMISSION_GRANTED = "permission granted"
const val ANIMATION_TIME_OUT: Long = 200
const val SELECTALL = "SELECT_ALL"
const val CLEARALL = "CLEAR_ALL"
const val PATH_CREATE_FOLDER = "path_Create_folder"
const val DELETE_FAILED = "Deleting this file Fail"
const val PROCESS_TYPE = "process type"
const val UNKNOWN_ERROR = "unknown Error"
const val CUT_FACED_ERROR = "cut Faced an Error"
const val DIRECTORY_CATEGORY = "Category_key"
const val SHOW_MIMETYPE = "show_mimetype"
const val IMAGES = "images"
const val VIDEOS = "videos"
const val AUDIO = "audio"
const val DOCUMENTS = "documents"
const val ARCHIVES = "archives"
const val OTHERS = "others"
// what else should we count as an audio except "audio/*" mimetype
val extraAudioMimeTypes = arrayListOf("application/ogg")
val extraDocumentMimeTypes = arrayListOf(
    "application/pdf",
    "application/msword",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    "application/javascript"
)

val archiveMimeTypes = arrayListOf(
    "application/zip",
    "application/octet-stream",
    "application/json",
    "application/x-tar",
    "application/x-rar-compressed",
    "application/x-zip-compressed",
    "application/x-7z-compressed",
    "application/x-compressed",
    "application/x-gzip",
    "application/java-archive",
    "multipart/x-zip"
)
