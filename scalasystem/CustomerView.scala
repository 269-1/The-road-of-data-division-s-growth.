package com.liushuai.scalasystem

import scala.io.StdIn

class CustomerView {
  var loop=true
  var key=' '
  val customerService = new CustomerService()


  def mainMenu()={
    do{
      println("-----------------客户信息管理软件-----------------")
      println(" 1 添 加 客 户")
      println(" 2 修 改 客 户")
      println(" 3 删 除 客 户")
      println(" 4 客 户 列 表")
      println(" 5 退 出")
      println("请选择(1-5)：")
      key = StdIn.readChar()
      key match {
        case '1' => add
        case '2' => {
          println("请输入id编号")
          val id=StdIn.readInt()
          update(id)
        }
        case '3' => del
        case '4' => list
        case '5' => {
          this.loop = false
          customerService.conectionClose()
        }
      }
    }while(loop)
    println("你退出了软件系统...")
  }
  def update(id:Int): Unit ={
    println("---------------------修改客户---------------------")
    println("姓名：")
    val name = StdIn.readLine()
    println("性别：")
    val gender = StdIn.readChar()
    println("年龄：")
    val age = StdIn.readShort()
    println("电话：")
    val tel = StdIn.readLine()
    println("邮箱：")
    val email = StdIn.readLine()
    val customer = new Customer(id,name,gender,age,tel,email)
    customerService.update(customer)
  }
  def list(){
    println()
    println("---------------------------客户列表---------------------------")
    println("编号\t\t 姓名\t\t 性别\t\t 年龄\t\t 电话\t\t 邮箱")
    //for 遍历
    //1.获取到 CustomerSerivce 的 Customers ArrayBuffer
    val Customers =customerService.showList//函数没有参数的不能加（）,要使用同一个CustomerService对象才行，否则如果是创建一个新对象，对应的成员也会是新的
    //      for (cutomer <- Customers) {
    //        //重写 Customer 的 toString 方法，返回信息(并且格式化)
    //        println(cutomer)
    //      }
    println("-------------------------客户列表完成-------------------------")
  }
  def add(): Unit ={
    println()
    println("---------------------添加客户---------------------")
    println("姓名：")
    val name = StdIn.readLine()
    println("性别：")
    val gender = StdIn.readChar()
    println("年龄：")
    val age = StdIn.readShort()
    println("电话：")
    val tel = StdIn.readLine()
    println("邮箱：")
    val email = StdIn.readLine()
    //构建对象
    val Customer = new Customer(name,gender,age,tel,email)
    customerService.add(Customer)
    println("---------------------添加完成---------------------")
  }
  def del(): Unit ={
    println("---------------------删除客户---------------------")
    println("请选择待删除客户编号(-1 退出)：")
    val id = StdIn.readInt()
    if (id == -1) {
      println("---------------------删除没有完成---------------------")
      return
    }
    println("确认是否删除(Y/N)：")
    val choice = StdIn.readChar().toLower
    if (choice == 'y') {
      if (customerService.delete(id)) {
        println("---------------------删除完成---------------------")
        return
      }
    }
    println("---------------------删除没有完成---------------------")
  }
}

object CustomerView{//注意如果改变了代码=》重新编译  如果改变了主类名=》重新选择主类运行
  def main(args: Array[String]): Unit = {
    new CustomerView().mainMenu()
  }
}





















