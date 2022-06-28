package com.doorstep24.doorstep.dao

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.doorstep24.doorstep.models.CartModel

@Dao
interface CartDao
{
    @Query("select * from cart_table")
    fun getProductsFromCart(): LiveData<List<CartModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProductsIntoDb(products:CartModel)

    @Query("UPDATE cart_table SET quantity = :quantity WHERE productId = :productId")
    fun updateQuantity(productId:String, quantity:Long)

    @Query("DELETE FROM cart_table WHERE productId = :productId")
    fun deleteProduct(productId:String)

    @Query("DELETE FROM cart_table")
    fun deleteCart()
}

@Database(entities = [CartModel::class],version=2,exportSchema = false)
abstract class CartDatabase : RoomDatabase() {
    abstract val cartDao: CartDao
}

private lateinit var INSTANCE:CartDatabase

fun getDatabase(context: Context):CartDatabase
{
    if(!::INSTANCE.isInitialized)
    {
        INSTANCE=
            Room.databaseBuilder(context.applicationContext,CartDatabase::class.java,"cart_database")
                .fallbackToDestructiveMigration().build()

    }
    return INSTANCE
}