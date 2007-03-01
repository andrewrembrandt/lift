package net.liftweb.proto

/*                                                *\
  (c) 2006-2007 WorldWide Conferencing, LLC
  Distributed under an Apache License
  http://www.apache.org/licenses/LICENSE-2.0
\*                                                */

import net.liftweb.mapper.{Mapper, MappedField, IndexedField}
import java.sql.{ResultSet, Types}
import java.lang.reflect.Method
import net.liftweb.util.Helpers._
import java.util.Date

class MappedIntIndex[T](owner : Mapper[T]) extends MappedInt[T](owner) with IndexedField[int] {

  override def writePermission_? = false // not writable
  
  override def dbIndex_? = true

  override def dbAutogeneratedIndex_? = true
  
  override def defaultValue = -1
  
  def defined_? = i_get_! != defaultValue
  
  override def dbIndexFieldIndicatesSaved_? = {i_get_! != defaultValue}
  
  def makeKeyJDBCFriendly(in : int) = new Integer(in)
  
  def convertKey(in : String) : Option[int] = {
    if (in eq null) None
    try {
      val what = if (in.startsWith(name + "=")) in.substring((name + "=").length) else in
      Some(Integer.parseInt(what))
    } catch {
      case _ => {None}
    }
  }
  
  override def db_display_? = false
  
  def convertKey(in : int) : Option[int] = {
    if (in < 0) None
    else Some(in)
  }
  
  def convertKey(in : long) : Option[int] = {
    if (in < 0 || in > Integer.MAX_VALUE) None
    else Some(in.asInstanceOf[int])
  }
  
  def convertKey(in : AnyRef) : Option[int] = {
    if ((in eq null) || (in eq None)) None
    try {
      convertKey(in.toString)
    } catch {
      case _ => {None}
    }                                         
  }
}


class MappedInt[T](val owner : Mapper[T]) extends MappedField[int, T] {
  private var data : int = defaultValue
  def defaultValue = 0

  /**
   * Get the JDBC SQL Type for this field
   */
  def getTargetSQLType = Types.INTEGER

  protected def i_get_! = data
  
  protected def i_set_!(value : int) : int = {
    if (value != data) {
      data = value
      this.dirty_?( true)
    }
    data
  }
  override def readPermission_? = true
  override def writePermission_? = true
  
  def convertToJDBCFriendly(value: int): Object = new Integer(value)
      
      
  def getJDBCFriendly(field : String) = new Integer(get)

    def ::=(in : Any) : int = {
    in match {
      case n: int => this := n
      case n: Number => this := n.intValue
      case (n: Number) :: _ => this := n.intValue
      case Some(n: Number) => this := n.intValue
      case None => this := 0
      case (s: String) :: _ => this := toInt(s)
      case null => this := 0
      case s: String => this := toInt(s)
      case o => this := toInt(o)
    }
  }
  
  protected def i_obscure_!(in : int) = 0
  
  def buildSetActualValue(accessor : Method, inst : AnyRef, columnName : String) : (Mapper[T], AnyRef) => unit = {
    inst match {
      case null => {(inst : Mapper[T], v : AnyRef) => {val tv = getField(inst, accessor).asInstanceOf[MappedInt[T]]; tv.data = 0;}}
      case _ if (inst.isInstanceOf[Number]) => {(inst : Mapper[T], v : AnyRef) => {val tv = getField(inst, accessor).asInstanceOf[MappedInt[T]]; tv.data = if (v == null) 0 else v.asInstanceOf[Number].intValue}}
      case _ => {(inst : Mapper[T], v : AnyRef) => {val tv = getField(inst, accessor).asInstanceOf[MappedInt[T]]; tv.data = tryn(Integer.parseInt(v.toString))}}
    }
  }
  
  def buildSetLongValue(accessor : Method, columnName : String) : (Mapper[T], long, boolean) => unit = {
    {(inst : Mapper[T], v: long, isNull: boolean ) => {val tv = getField(inst, accessor).asInstanceOf[MappedInt[T]]; tv.data = v.asInstanceOf[int]}}
  }
  def buildSetStringValue(accessor : Method, columnName : String) : (Mapper[T], String) => unit  = {
    {(inst : Mapper[T], v: String ) => {val tv = getField(inst, accessor).asInstanceOf[MappedInt[T]]; tv.data = tryn(Integer.parseInt(v))}}
  }
  def buildSetDateValue(accessor : Method, columnName : String) : (Mapper[T], Date) => unit   = {
    {(inst : Mapper[T], v: Date ) => {val tv = getField(inst, accessor).asInstanceOf[MappedInt[T]]; tv.data = if (v == null) 0 else v.getTime.asInstanceOf[int]}}
  }
  def buildSetBooleanValue(accessor : Method, columnName : String) : (Mapper[T], boolean, boolean) => unit   = {
    {(inst : Mapper[T], v: boolean, isNull: boolean ) => {val tv = getField(inst, accessor).asInstanceOf[MappedInt[T]]; tv.data = if (v && !isNull) 1 else 0}}
  }
}

