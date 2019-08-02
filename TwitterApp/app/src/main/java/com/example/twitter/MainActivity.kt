package com.example.twitter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_ticket.view.*
import kotlinx.android.synthetic.main.tweets_ticket.view.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    var ListTweets=ArrayList<Ticket>()
    var adapter:MyTweetAdpater?=null
    var myemail:String?=null
    var UserUID:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var b:Bundle=intent.extras
        myemail=b.getString("email")
        UserUID=b.getString("uid")
        MobileAds.initialize(this, "ca-app-pub-8457007513680564~9657029528");
        // Dummy data

        ListTweets.add(Ticket("0","him","url","add"))


        adapter = MyTweetAdpater(this, ListTweets)
        lvTweets.adapter = adapter
        LoadPost()
    }

    inner class MyTweetAdpater:BaseAdapter {

        var listNotesAdpater = ArrayList<Ticket>()
        var context: Context? = null

        constructor(context: Context, listNotesAdpater: ArrayList<Ticket>) : super() {
            this.listNotesAdpater = listNotesAdpater
            this.context = context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            var mytweet = listNotesAdpater[p0]

            if (mytweet.TweetPersonUID.equals("add")) {
                var myView = layoutInflater.inflate(R.layout.add_ticket, null)
                myView.iv_attach.setOnClickListener( View.OnClickListener {
                    loadImage()
                })
                myView.iv_post.setOnClickListener( View.OnClickListener {
                    // upload to the server

                    myRef.child("posts").push().setValue(PostInfo(UserUID!!,
                        myView.etPost.text.toString(),
                        DownloadURL!!))
                    myView.etPost.setText("")
                })
                return myView
            } else if(mytweet.TweetPersonUID.equals("loading")){
                var myView = layoutInflater.inflate(R.layout.loading_ticket, null)
                return myView
            } else if(mytweet.TweetPersonUID.equals("ads")){
                var myView = layoutInflater.inflate(R.layout.ads_ticket, null)
                var mAdView = myView.findViewById(R.id.adView) as AdView
                val adRequest = AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
                return myView
            } else {
                var myView = layoutInflater.inflate(R.layout.tweets_ticket, null)
                //Load tweet ticket
                myView.txt_tweet.setText(mytweet.TweetText)

                Picasso.with(context).load(mytweet.TweetImageURL).into(myView.tweet_picture)

                myRef.child("Users").child(mytweet.TweetPersonUID)
                    .addValueEventListener(object :ValueEventListener{
                        override fun onDataChange(dataSnapshot: DataSnapshot?) {
                            try{
                                var td= dataSnapshot!!.value as HashMap<String,Any>

                                for(key in td.keys){

                                    var userInfo=td[key] as String
                                    if(key.equals("ProfileImage")){
                                        Picasso.with(context).load(userInfo).into(myView.picture_path)
                                    }else{
                                        myView.txtUserName.setText(userInfo)
                                    }
                                }

                            }catch (ex:Exception){}
                        }

                        override fun onCancelled(p0: DatabaseError?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }
                    })

                return myView
            }

        }
        override fun getItem(position: Int): Any {
            return listNotesAdpater[position] //To change body of created functions use File | Settings | File Templates.
        }

        override fun getItemId(position: Int): Long {
            return position.toLong() //To change body of created functions use File | Settings | File Templates.
        }

        override fun getCount(): Int {
            return listNotesAdpater.size //To change body of created functions use File | Settings | File Templates.
        }
    }

    // Load images
    val PICK_IMAGE_CODE=123
    fun loadImage(){

        var intent= Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==PICK_IMAGE_CODE && data!=null && resultCode==RESULT_OK){

            val selectedImage=data.data
            val filePathColumn=arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage,filePathColumn,null,null,null)
            cursor.moveToFirst()
            val columnIndex=cursor.getColumnIndex(filePathColumn[0])
            val picturePath=cursor.getString(columnIndex)
            cursor.close()
            UploadImage(BitmapFactory.decodeFile(picturePath))
        }
    }

    var DownloadURL:String?=null
    fun UploadImage(bitmap: Bitmap){
        ListTweets.add(0, Ticket("0","him","url","loading"))
        adapter!!.notifyDataSetChanged()
        val storage= FirebaseStorage.getInstance()
        val storageRef=storage.getReferenceFromUrl("gs://tictactoeoline-3bfcf.appspot.com")
        val df= SimpleDateFormat("ddMMyyHHmmss")
        val dataobj = Date()
        val imagePath = SplitString(myemail!!) + "." + df.format(dataobj)+ ".jpg"
        val ImageRef = storageRef.child("imagePost/"+imagePath)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data = baos.toByteArray()
        val uploadTask = ImageRef.putBytes(data)
        uploadTask.addOnFailureListener{
            Toast.makeText(applicationContext,"fail to upload", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener { taskSnapshot ->
            DownloadURL = taskSnapshot.downloadUrl!!.toString()
            ListTweets.removeAt(0)
            adapter!!.notifyDataSetChanged()
        }
    }

    fun SplitString(email:String):String{
        val split= email.split("@")
        return split[0]
    }

    fun LoadPost(){
        myRef.child("posts")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                     try{

                         ListTweets.clear()
                         ListTweets.add(Ticket("0","him","url","add"))
                         ListTweets.add(Ticket("0","him","url","ads"))
                         var td= dataSnapshot!!.value as HashMap<String,Any>
                         for(key in td.keys){

                             var post=td[key] as HashMap<String,Any>

                             ListTweets.add(Ticket(key,

                                 post["text"] as String,
                                 post["postImage"] as String,
                                 post["userUID"] as String))
                         }
                         adapter!!.notifyDataSetChanged()
                     }catch (ex:Exception){}
                }

                override fun onCancelled(p0: DatabaseError?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
    }
}
