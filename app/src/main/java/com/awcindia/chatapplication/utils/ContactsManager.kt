package com.awcindia.chatapplication.utils

import android.content.Context
import android.provider.ContactsContract
import com.awcindia.chatapplication.model.Contact

class ContactsManager(val context: Context) {

    fun getContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
        )

        cursor?.use {
            val phoneNumberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val displayNameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

            while (it.moveToNext()) {
                val phoneNumber = it.getString(phoneNumberIndex)?.replace("+91", "")?.replace(" ", "")?.trim() ?: ""
                val contactName = it.getString(displayNameIndex) ?: "Unknown"
                contacts.add(Contact(contactName, phoneNumber))
            }
        }

        return contacts
    }
}

