// INotificationSideChannel.aidl
package com.qichuang.core.aidl;

// Declare any non-default types here with import statements

/**
 * Interface used for delivering notifications via a side channel that bypasses
 * the NotificationManagerService.
 *
 * @hide
 */
oneway interface INotificationSideChannel {
    /**
     * Send an ambient notification to the service.
     */
    void notify(String packageName, int id, String tag, in Notification notification);

    /**
     * Cancel an already-notified notification.
     */
    void cancel(String packageName, int id, String tag);

    /**
     * Cancel all notifications for the given package.
     */
    void cancelAll(String packageName);
}