package com.hzq.ormsample

import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.hzq.db.room.entity.ProductEntity
import com.hzq.ormsample.helper.coroutine
import com.hzq.ormsample.helper.dbHelper
import kotlinx.android.synthetic.main.activity_create_product.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast

class CreateProductActivity : BaseActivity() {

    var id: Long = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_product)
        initToolBar()
        id = intent.getLongExtra("id", -1)
        if(isIdNull()) title = "create product" else title = "update product"
        delete.visibility = if(isIdNull()) View.GONE else View.VISIBLE
        delete.setOnClickListener {
            coroutine({
                dbHelper.delete(id)
            }){
                toast("delete success!")
                EventBus.getDefault().post(OnEventChenge(1))
                finish()
            }
        }
        if(!isIdNull()) {
            coroutine({
                dbHelper.getProductById(id)
            }) {
                with(it) {
                    inputName.setText(name)
                    inputDesc.setText(description)
                    inputPrice.setText(price.toString())
                }
            }
        }
    }

    fun isIdNull(): Boolean{
        return id == null || "-1".equals(id.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.task_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        save()
        return super.onOptionsItemSelected(item)
    }

    fun save(){
        var p = ProductEntity()
        if(!isIdNull()){
            p.id = id
        }
        p.name = inputName.text.toString()
        p.description = inputDesc.text.toString()
        p.price = if(!TextUtils.isEmpty(inputPrice.text.toString())) inputPrice.text.toString().toDouble() else 0.0

        coroutine({
            if(!isIdNull()) dbHelper.updateProduct(p) else  dbHelper.insert(p)
        }){
            if(!isIdNull()) toast("save success") else toast("update success")
            EventBus.getDefault().post(OnEventChenge(1))
            finish()
        }

    }
}
