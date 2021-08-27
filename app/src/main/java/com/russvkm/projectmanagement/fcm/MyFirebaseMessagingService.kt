package com.russvkm.projectmanagement.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.russvkm.projectmanagement.R
import com.russvkm.projectmanagement.activity.MainActivity
import com.russvkm.projectmanagement.activity.SignInActivity
import com.russvkm.projectmanagement.firebase.FireStoreHandler
import com.russvkm.projectmanagement.utils.Constants

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.i(TAG,"From: ${remoteMessage.from}")
        remoteMessage.data.isNotEmpty().let {
            Log.i(TAG,"Message Data Payload: ${remoteMessage.data}")

            val title=remoteMessage.data[Constants.FCM_KEY_TITLE]!!
            val message=remoteMessage.data[Constants.FCM_KEY_MESSAGE]!!

            sendNotification(title,message)

        }

        remoteMessage.notification.let {
            Log.i(TAG,"Message Notification Body: ${it?.body}")
        }


    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.e(TAG,"refreshed Token $token")
    }

    private fun sendRegistrationServer(token:String?){

    }

    private fun sendNotification(title:String,message:String){
        val intent=if (FireStoreHandler().userId().isNotEmpty()){
            Intent(this,MainActivity::class.java)
        }else{
            Intent(this,SignInActivity::class.java)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent=PendingIntent.getActivity(this,
        0,intent,PendingIntent.FLAG_ONE_SHOT)
        val channelId=this.resources.getString(R.string.default_notification_channel_id)
        val defaultRingTone=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder=NotificationCompat.Builder(
            this,channelId
        ).setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setSound(defaultRingTone)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager=getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            val channel=NotificationChannel(channelId
            ,"project management title",NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0,notificationBuilder.build())
    }

    companion object{
        private const val TAG="MyFirebaseMsgService"
    }
}