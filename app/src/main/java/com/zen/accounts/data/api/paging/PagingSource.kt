package com.zen.accounts.data.api.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.zen.accounts.data.db.model.Expense
import kotlinx.coroutines.tasks.await

class PagingSource: PagingSource<DocumentSnapshot, Expense>() {
    private val colRef = FirebaseFirestore.getInstance().collection("Users").document("95288Roop@roopkm1265314").collection("expenses")
    override fun getRefreshKey(state : PagingState<DocumentSnapshot, Expense>) : DocumentSnapshot? {
        return null
    }
    
    override suspend fun load(params : LoadParams<DocumentSnapshot>) : LoadResult<DocumentSnapshot, Expense> {
        return try {
            val data = if (params.key != null) {
                getExpense(params.key!!)
            } else {
                getFirstPage()
            }
            
            LoadResult.Page(
                data.second,
                params.key,
                data.first
            )
            
        } catch (e : Exception) {
            LoadResult.Error(e)
        }
    }
    
    private suspend fun getExpense(docSnapshot: DocumentSnapshot) : Pair<DocumentSnapshot, List<Expense>> {
        val request = colRef.limit(10).startAfter(docSnapshot).get()
        val result = request.await()
        val lastVisibleDoc = result.documents[result.size() - 1]
        return if(request.isSuccessful) {
            Pair(
                lastVisibleDoc, result.toObjects(Expense::class.java)
            )
        } else {
            throw  Exception("Not Successful")
        }
    }
    
    private suspend fun getFirstPage() : Pair<DocumentSnapshot, List<Expense>>{
        val request = colRef.limit(10).get()
        val result = request.await()
        val lastVisibleDoc = result.documents[result.size() - 1]
        return if(request.isSuccessful) {
            Pair(
                lastVisibleDoc, result.toObjects(Expense::class.java)
            )
        } else {
            throw  Exception("Not Successful")
        }
    }
    
}
