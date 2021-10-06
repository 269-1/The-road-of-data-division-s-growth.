package com.liushuai.scalasystem

import java.sql.{ DriverManager}

import com.atguigu.scala_item.CustomerService.CustomerNum

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks.{break, breakable}

class CustomerService {
  //建立与mysql的连接
  var url = "jdbc:mysql://localhost:3306/test"
  var driver = "com.mysql.cj.jdbc.Driver"
  var username = "root"
  var password = "sab284365"
  Class.forName(driver)
  var connection = DriverManager.getConnection(url, username, password)
  val statement = connection.createStatement
  var Customers=ArrayBuffer[Customer]()
  //获取当前数据库的行数用于之后的更新
  CustomerNum=idLength

  //或者当前表中的最后一项id值（行数）
  def idLength()={
    val rs=statement.executeQuery("select count(*)  num from scala_test")
    var ans=0
    while(rs.next()) {
      ans=rs.getInt("num")
    }
    ans
  }
  //关闭mysql连接
  def conectionClose(): Unit ={
    connection.close()
  }
  //当前所有项展示
  def showList={
    val rs=statement.executeQuery("select*from scala_test")//executeQuery内放置查询语句
    while(rs.next()){
      val id: Int = rs.getInt("id")
      val name: String = rs.getString("name")
      val gender: String = rs.getString("gender")
      val age: Int = rs.getInt("age")
      val tel: String = rs.getString("tel")
      val email:String=rs.getString("email")
      println(id + "\t" + name + "\t" + gender + "\t" + age + "\t" + tel+"\t"+email)
    }
  }
  //增加某一项
  def add(Customer:Customer)={
    try {
      CustomerService.CustomerNum += 1
      Customer.id = CustomerService.CustomerNum
      Customers.append(Customer)
      val st=statement.executeUpdate(s"insert into scala_test values(${Customer.id},${Customer.name},${Customer.gender},${Customer.age},${Customer.tel},${Customer.email})")
    }catch{
      case ex:Exception=>{
        false
      }
    }
    true
  }
  //删除某一项
  def delete(the_id:Int)={
    try {
      statement.executeUpdate(s"delete from scala_test where id=${the_id}")
      statement.executeUpdate(s"update scala_test set id=id-1 where id>=${the_id}")//删除当前元素之后就将后面的元素的id--
      CustomerNum-=1
    }catch{
      case ex:Exception=>false
    }
    true
    //    对象数组保存的删除方式
    //      val index=findIndexById(id)
    //    if(index != -1){
    //      Customers.remove(index)
    //      true
    //    }else{
    //      false
    //    }
  }
  //更新数据库
  def update(cust:Customer): Unit ={
    try{
    statement.executeUpdate(s"update scala_test set name=${cust.name},gender=${cust.gender},age=${cust.age}," +
      s"tel=${cust.tel},email=${cust.email} where id=${cust.id}")
    }catch{
      case ex:Exception=>{
        ex.printStackTrace()
      }
    }
  }
}

object CustomerService{
  var CustomerNum=0
}
