//package com.ahmed.homeservices.models;
//
//import androidx.annotation.NonNull;
//import androidx.room.ColumnInfo;
//import androidx.room.Entity;
//import androidx.room.PrimaryKey;
//
//import com.ahmed.homeservices.constants.Constants;
//
//
////@Table(name = "BackupsTable")//for sugar database
//@Entity(tableName = Constants.TABLE_NAME)//for room database
////@TypeConverters(BackupsTypeConverter.class)
////public class NOTIFI_ITEM extends SugarRecord {
//public class NOTIFI_ITEM {
////        implements Serializable {
//
//
//    //    @PrimaryKey(autoGenerate = true)
//    @PrimaryKey
//    @NonNull
////    @ColumnInfo(name = "backup_id")
//    private String order_number;
//
//
//    @ColumnInfo(name = "order_name")
//    @NonNull
//    //@Ignore
////    @TypeConverters(BackupsTypeConverter.class)
////    private List<ContactData> contactDataList;
//    private String order_name;
//
//    @ColumnInfo(name = "backup_date")
//    @NonNull
//    private String backupDate;
//
//
////    @ColumnInfo(name = "uploadedOrNot")
////    @NonNull
////    private boolean uploadedOrNot = false;
//
//    //this is for sugar orm - important
//    //but not important for room database
//    public NOTIFI_ITEM() {
//
//
//    }
//
//
////    public boolean isUploadedOrNot() {
////        return uploadedOrNot;
////    }
////
////    public void setUploadedOrNot(boolean uploadedOrNot) {
////        this.uploadedOrNot = uploadedOrNot;
////    }
//
//
//    //=============================================
//
////    @Column(name = "contactDataList")
////    private
////    List<ContactData> contactDataList;
////
////    @Column(name = "backup_date")
////    private
////    String backupDate;
//
//
//    @NonNull
//    public String getOrder_number() {
//        return order_number;
//    }
//
//    public void setOrder_number(@NonNull String order_number) {
//        this.order_number = order_number;
//    }
//
//    @NonNull
//    public String getOrder_name() {
//        return order_name;
//    }
//
//    public void setOrder_name(@NonNull String order_name) {
//        this.order_name = order_name;
//    }
//
//    @NonNull
//    public String getBackupDate() {
//        return backupDate;
//    }
//
//    public void setBackupDate(@NonNull String backupDate) {
//        this.backupDate = backupDate;
//    }
//
//
//}
