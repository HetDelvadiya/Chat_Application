    package com.awcindia.chatapplication.utils

    import android.content.Context
    import android.provider.ContactsContract
    import com.awcindia.chatapplication.model.Contact

    class ContactsManager(val context: Context) {

        private val contacts = mutableListOf<Contact>()

        fun getContacts(): List<Contact> {

            val cursor = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
            )

            cursor?.let {
                while (it.moveToNext()) {
                    val nameIndex =
                        it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                    if (nameIndex > -1 && numberIndex > -1) {
                        val contactName = it.getString(nameIndex) ?: "Unknown"
                        val phoneNumber = it.getString(numberIndex)
                            .replace("+91", "")
                            .replace(" ", "")
                            .trim()


                        if (contactName.isNotBlank() && phoneNumber.isNotBlank()) {
                            contacts.add(Contact(contactName, phoneNumber))
                        }
                    }
                }
            }

            return contacts
        }
    }