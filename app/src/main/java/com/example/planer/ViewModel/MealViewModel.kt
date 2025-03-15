package com.example.planer.ViewModel

import androidx.lifecycle.ViewModel

class MealViewModel : ViewModel() {

    var mealId: Long = 0
    var filePath = ""

//    //todo singelton
//    companion object {
//        var mealId: Long = 0
//        var filePath = ""
//
//        @Volatile
//        private var instance: MealViewModel? = null
//
//        fun getInstance(): MealViewModel {
//            return instance ?: synchronized(this) {
//                instance ?: MealViewModel().also { instance = it }
//            }
//        }
//    }

}