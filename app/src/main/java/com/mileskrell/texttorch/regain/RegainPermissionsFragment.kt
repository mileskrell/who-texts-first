package com.mileskrell.texttorch.regain

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mileskrell.texttorch.R
import com.mileskrell.texttorch.util.PERMISSIONS_REQUEST_CODE
import com.mileskrell.texttorch.util.readContactsGranted
import com.mileskrell.texttorch.util.readSmsGranted
import com.mileskrell.texttorch.util.showAppSettingsDialog
import kotlinx.android.synthetic.main.fragment_regain_permissions.*

/**
 * This page is opened if the user has completed the tutorial, but we don't have all the permissions
 * we need. Since the user has to grant permissions to complete the tutorial, this indicates that at
 * some point, the user went and manually denied these permissions.
 *
 * This won't happen very often, but it's still important that we handle it properly.
 *
 * This page can be opened either on app start (by IntroFragment) or in AnalyzeFragment's
 * onCreateView.
 */
class RegainPermissionsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_regain_permissions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        regain_button.setOnClickListener {
            requestPermissions(
                arrayOf(Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED } ) {
                    findNavController().navigate(R.id.regain_to_analyze_action)
                } else {
                    // Not all permissions were granted
                    val canAskAgain = (readSmsGranted() || shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS))
                            && (readContactsGranted() || shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS))
                    if (!canAskAgain) {
                        // User checked "Never ask again", so open app settings page
                        showAppSettingsDialog()
                    }
                }
            }
        }
    }

    /**
     * Called when the user returns from the app settings page. This makes it so the user doesn't
     * need to tap the "regrant permissions" button when they return.
     */
    override fun onResume() {
        super.onResume()
        if (readSmsGranted() && readContactsGranted()) {
            // The user finally granted the permissions! Continue to AnalyzeFragment.
            findNavController().navigate(R.id.regain_to_analyze_action)
        }
    }
}
