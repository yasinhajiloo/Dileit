package com.yasinhajilou.dileit.view.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.yasinhajilou.dileit.R
import com.yasinhajilou.dileit.constant.URIConstants
import com.yasinhajilou.dileit.databinding.ActivityAboutBinding
import com.yasinhajilou.dileit.view.adapter.recycler.ContributorsRecyclerAdapter
import com.yasinhajilou.dileit.viewmodel.NetworkViewModel


class AboutActivity : AppCompatActivity() {
    private lateinit var networkViewModel: NetworkViewModel
    private lateinit var mBinding: ActivityAboutBinding
    val TAG = AboutActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        networkViewModel = ViewModelProvider(this).get(NetworkViewModel::class.java)

        val rvAdapter = ContributorsRecyclerAdapter()
        //call for getting data
        networkViewModel.getContributors()

        mBinding.rvContributor.apply {
            adapter = rvAdapter
            layoutManager = GridLayoutManager(context, 3)
        }
        networkViewModel.liveContributorList.observe(this, {
            Log.d(TAG, "onCreate: ${it.toString()}")
            rvAdapter.setData(it)
        })

        mBinding.ivShare.setOnClickListener {
            shareApp()
        }

        mBinding.ivGithub.setOnClickListener { openBrowser(URIConstants.githubUrl) }
        mBinding.ivInstagram.setOnClickListener { openInstagram() }
        mBinding.ivTelegram.setOnClickListener { openBrowser(URIConstants.telegramURL) }
        mBinding.lottieAnimRating.setOnClickListener { rateApp() }
        mBinding.message.setOnClickListener { sendEmail() }
        mBinding.ivBackAbout.setOnClickListener { this.finish() }

        mBinding.ivCopyRight.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this)
                    .setMessage(resources.getString(R.string.copy_right))
                    .setCancelable(true)
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val shareContent = resources.getString(R.string.share_body)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_diliet))
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent)
        startActivity(Intent.createChooser(shareIntent, resources.getString(R.string.share_via)))
    }

    private fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun openInstagram() {
        val uri = Uri.parse(URIConstants.instagramURL)
        val instaIntent = Intent(Intent.ACTION_VIEW, uri)
        instaIntent.setPackage(URIConstants.instagramPackageURI)

        if (isIntentAvailable(instaIntent)) {
            startActivity(instaIntent)
        } else {
            val newURI = Uri.parse(URIConstants.instagramURL)
            val newIntent = Intent(Intent.ACTION_VIEW, newURI)
            startActivity(newIntent)
        }
    }

    private fun isIntentAvailable(intent: Intent): Boolean {
        val packageManager: PackageManager = packageManager
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
    }

    private fun rateApp() {
//        val intent = Intent()
//        intent.action = Intent.ACTION_VIEW
//        intent.data = Uri.parse("myket://comment?id=" + applicationContext.packageName)
//        if (isIntentAvailable(intent)) {
//            startActivity(intent)
//        } else {
//            //check for Charkhoone :
//            intent.data = Uri.parse("jhoobin://comment?q=" + applicationContext.packageName)
//            if (isIntentAvailable(intent)) {
//                startActivity(intent)
//            } else {
//                Toast.makeText(this, resources.getString(R.string.no_app_rating), Toast.LENGTH_LONG).show()
//            }
//
//        }
        val packageManager = applicationContext.packageManager
        val intent = Intent(Intent.ACTION_EDIT)
        intent.data = Uri.parse("bazaar://details?id=" + applicationContext.packageName)
        intent.setPackage("com.farsitel.bazaar")
        if (intent.resolveActivity(packageManager) != null) startActivity(intent) else {
            Toast.makeText(this, resources.getString(R.string.no_app_rating), Toast.LENGTH_LONG).show()        }
    }

    private fun sendEmail() {
        val i = Intent(Intent.ACTION_SENDTO)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(URIConstants.email))
        i.data = Uri.parse("mailto:");
        i.putExtra(Intent.EXTRA_SUBJECT, "Dileit Recommendations & Critics")
        try {
            startActivity(Intent.createChooser(i, "Send mail..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, resources.getString(R.string.no_mail), Toast.LENGTH_SHORT).show()
        }
    }
}