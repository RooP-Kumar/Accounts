package com.zen.accounts.data.api

import com.google.firebase.firestore.FirebaseFirestore
import com.zen.accounts.data.api.resource.Response
import com.zen.accounts.data.db.model.Expense
import java.util.Calendar
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.abs
import kotlin.math.floor

class ExpenseApi @Inject constructor() {
    suspend fun uploadToFirebase(uid : String, expense : List<Expense>) : Response<Unit> =
        suspendCoroutine { continuation ->
            val response = Response(value = Unit)
            val db = FirebaseFirestore.getInstance()
            val colRef = db.collection("Users").document(uid).collection("expenses")
            db.runTransaction { trans ->
                expense.forEach { expense ->
                    trans.set(colRef.document(expense.id.toString()), expense)
                }
            }
                .addOnSuccessListener {
                    response.status = true
                    response.message = it.toString()
                    continuation.resume(response)
                }
                .addOnFailureListener {
                    response.status = false
                    response.message = it.message.toString()
                    continuation.resume(response)
                }
        }
    
    suspend fun getExpenseFromFirebase(uid : String) : Response<List<Expense>> =
        suspendCoroutine { continuation ->
            val response = Response(value = listOf<Expense>())
            val db = FirebaseFirestore.getInstance()
            db.collection("Users").document(uid).collection("expenses")
                .get()
                .addOnSuccessListener {
                    response.value = it.toObjects(Expense::class.java)
                    response.status = true
                    response.message = "Success!"
                    continuation.resume(response)
                }
                .addOnFailureListener {
                    response.status = false
                    response.message = it.message.toString()
                    continuation.resume(response)
                }
        }
    
    suspend fun deleteFromFirebase(uid : String, expenseIds : List<Long>) : Response<Unit> =
        suspendCoroutine { continuation ->
            val response = Response(value = Unit)
            val db = FirebaseFirestore.getInstance()
            val colRef = db.collection("Users").document(uid).collection("expenses")
            db.runTransaction { trans ->
                expenseIds.forEach { id ->
                    trans.delete(colRef.document(id.toString()))
                }
            }
                .addOnSuccessListener {
                    response.status = true
                    response.message = it.toString()
                    continuation.resume(response)
                }
                .addOnFailureListener {
                    response.status = false
                    response.message = it.message.toString()
                    continuation.resume(response)
                }
        }
    
    suspend fun getLastSixMonthExpense(uid : String) : Response<List<Double>> =
        suspendCoroutine { continuation ->
            val response = Response(value = listOf(0.0))
            val db = FirebaseFirestore.getInstance()
            val colRef = db.collection("Users").document(uid).collection("expenses")
            
            val currentCal = Calendar.getInstance()
            val previousCal = Calendar.getInstance()
            previousCal.add(Calendar.MONTH, -5)
            
            val currentMonth = currentCal.get(Calendar.MONTH)
            
            colRef
                .whereGreaterThan("date", previousCal.time)
                .whereLessThan("date", currentCal.time)
                .orderBy("date")
                .get()
                .addOnSuccessListener {
                    val tempCal = Calendar.getInstance()
                    tempCal.add(Calendar.MONTH, -5)

                    val amountList = Array(6){0.0}
                    
                    it.toObjects(Expense::class.java).forEach { expense ->
                        expense.date?.let { date ->
                            val tCal = Calendar.getInstance()
                            tCal.time = date
                            
                            amountList[abs(6 - abs(currentMonth - tCal.get(Calendar.MONTH))) - 1] += expense.totalAmount
                        }
                    }
 
                    response.value = amountList.toList()
                    response.status = true
                    response.message = "Success!"
                    continuation.resume(response)
                }
                .addOnFailureListener {
                    response.status = false
                    response.message = it.message.toString()
                    continuation.resume(response)
                }
            
        }
    
    suspend fun getCurrentWeekExpense(uid : String) : Response<List<Double>> =
        suspendCoroutine { continuation ->
            val response = Response(value = listOf(0.0))
            val db = FirebaseFirestore.getInstance()
            val colRef = db.collection("Users").document(uid).collection("expenses")
            
            val currentCal = Calendar.getInstance()
            val previousCal = Calendar.getInstance()
            previousCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            
            colRef
                .whereGreaterThan("date", previousCal.time)
                .whereLessThan("date", currentCal.time)
                .orderBy("date")
                .get()
                .addOnSuccessListener {
                    val amountList = Array(currentCal.get(Calendar.DAY_OF_WEEK)) {0.0}
                    
                    it.toObjects(Expense::class.java).forEach { expense ->
                        expense.date?.let { date ->
                            val tCal = Calendar.getInstance()
                            tCal.time = date
                            val tCalWeek = tCal.get(Calendar.DAY_OF_WEEK)
                            amountList[tCalWeek-1] += expense.totalAmount
                        }
                    }
                    
                    response.value = amountList.toList()
                    response.status = true
                    response.message = "Success!"
                    continuation.resume(response)
                }
                .addOnFailureListener {
                    response.status = false
                    response.message = it.message.toString()
                    continuation.resume(response)
                }
            
        }
    
    suspend fun getTodayExpense(uid : String) : Response<List<Double>> = suspendCoroutine { continuation -> 
        val response = Response(value = listOf(0.0))
        val db = FirebaseFirestore.getInstance()
        val colRef = db.collection("Users")
        val docRef = colRef.document(uid).collection("expenses")
        val startCal = Calendar.getInstance()
        startCal.set(Calendar.HOUR_OF_DAY, 0)
        startCal.set(Calendar.MINUTE, 0)
        startCal.set(Calendar.SECOND, 0)
        
        val endCal = Calendar.getInstance()
        endCal.set(Calendar.HOUR_OF_DAY, 23)
        endCal.set(Calendar.MINUTE, 59)
        endCal.set(Calendar.SECOND, 59)
        val currentCal = Calendar.getInstance()
        val amountList = Array<Double>(currentCal.get(Calendar.HOUR_OF_DAY)/4 + 1) { 0.0 }
        docRef
            .whereGreaterThan("date", startCal.time)
            .whereLessThan("date", endCal.time)
            .get()
            .addOnSuccessListener {
                it.documents.forEach { documentSnapshot ->
                    documentSnapshot.toObject(Expense::class.java)?.let { exp ->
                        val tempCal = Calendar.getInstance()
                        tempCal.time = exp.date!!
                        
                        amountList[floor(tempCal.get(Calendar.HOUR_OF_DAY)/4.0).toInt()] += exp.totalAmount

                    }
                }
                response.value = amountList.toList()
                response.status = true
                response.message = "Success!"
                continuation.resume(response)
            }
            .addOnFailureListener {
                response.status = false
                response.message = it.message.toString()
                continuation.resume(response)
            }
    }
}
