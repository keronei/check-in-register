package com.keronei.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.RESTRICT
import androidx.room.PrimaryKey
import java.lang.reflect.Field

@Entity(
    foreignKeys = [ForeignKey(
        entity = RegionDBO::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("regionId"),
        onDelete = RESTRICT
    )]
)
data class MemberDBO(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val firstName: String,

    val secondName: String,

    val otherNames: String,

    val sex: Int,

    val birthYear: Int,

    @ColumnInfo(defaultValue = "0")
    val isMarried : Boolean,

    val phoneNumber: String?,

    val isActive: Boolean,

    @ColumnInfo(index = true)
    val regionId: Int
)

//TableInfo{name='MemberDBO', columns={isMarried=Column{name='isMarried', type='BOOLEAN', affinity='1', notNull=true, primaryKeyPosition=0, defaultValue=''false''}, firstName=Column{name='firstName', type='TEXT', affinity='2', notNull=true, primaryKeyPosition=0, defaultValue='null'}, otherNames=Column{name='otherNames', type='TEXT', affinity='2', notNull=true, primaryKeyPosition=0, defaultValue='null'}, phoneNumber=Column{name='phoneNumber', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='null'}, regionId=Column{name='regionId', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='null'}, sex=Column{name='sex', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='null'}, id=Column{name='id', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=1, defaultValue='null'}, isActive=Column{name='isActive', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='null'}, age=Column{name='age', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='null'}, secondName=Column{name='secondName', type='TEXT', affinity='2', notNull=true, primaryKeyPosition=0, defaultValue='null'}}, foreignKeys=[ForeignKey{referenceTable='RegionDBO', onDelete='RESTRICT', onUpdate='NO ACTION', columnNames=[regionId], referenceColumnNames=[id]}], indices=[Index{name='index_MemberDBO_regionId', unique=false, columns=[regionId], orders=[ASC]}]}
//TableInfo{name='MemberDBO', columns={isMarried=Column{name='isMarried', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='false'}, firstName=Column{name='firstName', type='TEXT', affinity='2', notNull=true, primaryKeyPosition=0, defaultValue='null'}, otherNames=Column{name='otherNames', type='TEXT', affinity='2', notNull=true, primaryKeyPosition=0, defaultValue='null'}, phoneNumber=Column{name='phoneNumber', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='null'}, birthYear=Column{name='birthYear', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='null'}, regionId=Column{name='regionId', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='null'}, sex=Column{name='sex', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='null'}, id=Column{name='id', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=1, defaultValue='null'}, isActive=Column{name='isActive', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='null'}, secondName=Column{name='secondName', type='TEXT', affinity='2', notNull=true, primaryKeyPosition=0, defaultValue='null'}}, foreignKeys=[ForeignKey{referenceTable='RegionDBO', onDelete='RESTRICT', onUpdate='NO ACTION', columnNames=[regionId], referenceColumnNames=[id]}], indices=[Index{name='index_MemberDBO_regionId', unique=false, columns=[regionId], orders=[ASC]}]}