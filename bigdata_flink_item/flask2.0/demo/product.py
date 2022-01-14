#!/usr/bin/python
# -*- coding: UTF-8 -*-
import time
import pymysql
import json
class Product:
    def __init__(self):
        #pymaysql两个对象，1.connect对象，用于连接数据库2.cursor 游标对象，用于执行sql语句
        self.db = pymysql.connect(host="hadoop102",user="root", password="123456",database= "TESTDB",port=8066)
        cursor = self.db.cursor()
        #我们要写入json文件中的数据就是ans
        ans=[]# 可以直接使用dict的keys,values直接得到key和values,之后的HTML页面就不需要再使用遍历获取了;
        ans.append({'CX': [], 'number': []})
        ans.append({'SFZCKMC': [], 'numSber': []})
        ans.append({'SFZRKMC': [], 'number': []})
        ans.append({'city': [], 'number': []})
        ans.append({'city_number': [], 'city_jinwei': {}})
        ans.append({'number':[]})
        ans.append({'number':[]})
        ans.append(0)
        data_begin={}
        data_begin1={}
        data_begin2={}
        data_begin3={}
        data_begin4={}
        last_number=-1
        with open('sult.text',mode='r',encoding='utf-8') as f:
            sult=f.read()
        sult=eval(sult)#转为字典
        while(True):
            #避免重复查询
            cursor.execute("select count(*) from expressway")
            data=cursor.fetchall()
            ans[7]=int(data[0][0])
            #避免重复空值写入
            if data[0][0]==0 and last_number==0:
                time.sleep(5)
                continue
            if data[0][0]==0 and last_number==-1:
                last_number=0
                time.sleep(5)
                continue
            else:
                #避免重复值写入
                if data[0][0]==last_number:
                    continue
                elif data[0][0]!=last_number:
                    last_number=data[0][0]
                # 每秒钟车辆类型
                cursor.execute("select CX,count(CX) from expressway group by(CX)")  # 执行查询语句,只能使用一次
                data = dict(cursor.fetchall())
                if data_begin.items()!=data.items():
                    data_begin=data
                    data=dict(sorted(data.items(),key=lambda x:x[1],reverse=True)[:15])
                    ans[0].update({'CX': list(data.keys()), 'number': list(data.values())})
                    list_number = []
                    sum1=sum(list(data.values()))
                    for i in data.keys():
                        list_number.append({'value': (float(data[i])*100/sum1).__format__('.2f'), 'name': i})
                    ans[5].update({'number': list_number})
                # 每秒钟各收费站出口车辆经过数
                cursor.execute("select SFZCKMC,count(SFZCKMC) from expressway group by(SFZCKMC)")#执行查询语句,只能使用一次
                data=dict(cursor.fetchall())
                if data_begin1.items()!=data.items():
                    data_begin1=data
                    data = dict(sorted(data.items(), key=lambda x: x[1], reverse=True)[:15])
                    ans[1].update({'SFZCKMC': list(data.keys()), 'number': list(data.values())})
                    list_number = []
                    sum1=sum(list(data.values()))
                    for i in data.keys():
                        list_number.append({'value': (float(data[i])*100/sum1).__format__('.2f'), 'name': i})
                    ans[6].update({'number': list_number})
                # 每秒钟各收费站入口车辆经过数
                cursor.execute("select SFZRKMC,count(SFZCKMC) from expressway group by(SFZRKMC)")#执行查询语句,只能使用一次
                data=dict(cursor.fetchall())
                if data_begin2.items()!=data.items():
                    data_begin2=data
                    data1 = dict(sorted(data.items(), key=lambda x: x[1], reverse=True)[:15])
                    ans[2].update({'SFZRKMC': list(data1.keys()), 'number': list(data1.values())})
                #每s钟进出深圳的车辆来源前15
                cursor.execute("select substr(CP,1,2) city,count(*) from expressway group by substr(CP,1,2)")  # 执行查询语句,只能使用一次
                data = dict(cursor.fetchall())
                if data_begin3.items() != data.items():
                    data_begin3 = data
                    data1 = dict(sorted(data.items(), key=lambda x: x[1], reverse=True)[:15])
                    key=[]
                    for i in list(data1.keys()):
                            key.append(sult[i])
                    ans[3].update({'city': key, 'number': list(data1.values())})
                # 每s钟进出深圳的车辆来源地图分布
                cursor.execute("select substr(CP,1,2) city,count(*) from expressway group by substr(CP,1,2)")  # 执行查询语句,只能使用一次
                data = dict(cursor.fetchall())
                key1={}
                for i in list(data.keys()):
                    if sult.get(i) == None:
                        if i[:1] == '沪':
                            sult.update({i: '上海'})
                        elif i[:1]=='京':
                            sult.update({i:'北京'})
                        elif i[:1]=='吉':
                            sult.update({i:'吉林'})
                        elif i[:1]=='宁':
                            sult.update({i:'宁夏'})
                        elif i[:1] == '津':
                            sult.update({i: '天津'})
                        elif i[:1] == '桂':
                            sult.update({i: '广西'})
                        elif i[:1] == '浙':
                            sult.update({i: '浙江'})
                        elif i[:1] == '晋':
                            sult.update({i: '山西'})
                        elif i[:1] == '晋':
                            sult.update({i: '山西'})
                        elif i[:1] == '渝':
                            sult.update({i: '重庆'})
                        elif i[:1] == '琼':
                            sult.update({i: '海南'})
                        elif i[:1] == '甘':
                            sult.update({i: '甘肃'})
                        elif i[:1] == '港':
                            sult.update({i: '香港'})
                        elif i[:1] == '粤':
                            sult.update({i: '广东'})
                        elif i[:1] == '湘':
                            sult.update({i: '湖南'})
                        elif i[:1] == '苏':
                            sult.update({i: '江苏'})
                        elif i[:1] == '蒙':
                            sult.update({i: '内蒙古自治区'})
                        elif i[:1] == '豫':
                            sult.update({i: '河南'})
                        elif i[:1] == '澳':
                            sult.update({i: '澳门'})
                        elif i[:1] == '闽':
                            sult.update({i: '福建'})
                        elif i[:1] == '赣':
                            sult.update({i: '江西'})
                        elif i[:1] == '陕':
                            sult.update({i: '陕西'})
                        elif i[:1] == '鲁':
                            sult.update({i: '山东'})
                        elif i[:1] == '黑':
                            sult.update({i: '黑龙江'})
                    #字典形式要注意使用:
                    if key1.get(sult[i])==None:
                        key1.update({sult[i]:data[i]})
                    else:
                        key1.update({sult[i]:key1[sult[i]]+data[i]})
                with open('jinwei.text',mode='r',encoding='utf-8') as f:
                    jinwei=eval(f.read())
                jinwei_end=[]
                #由keys得到其对应的经纬度
                for i in key1.keys():
                    if '市' in i or '区' in i:
                        jinwei_end.append(jinwei[i[:-1]])
                    else:
                        jinwei_end.append(jinwei[i])
                res1=dict(zip(key1.keys(),jinwei_end))
                #查询经纬度：
                if data_begin4.items() != data.items():#防止不停更新
                    data_begin4 = data
                    x=[]
                    #取前10
                    # key1=dict(sorted(key1.items(),reverse=True,key=lambda x:x[1])[0:10])
                    for i in key1.keys():
                        x.append({'name':i,'value':key1[i]})
                    #如果是第一次就使用append,否则直接更新4号位置的字典数据
                    ans[4].update({'city_number': x, 'city_jinwei': res1})
            #将数据放入到json文件中用于前台使用
            result=json.dumps(ans)
            with open (file="static/data.json",encoding='utf-8',mode='w') as f:
                f.write(result)
            #2s刷新一次数据
            time.sleep(0.5)#查询语句是边较消耗时间的
if __name__=='__main__':
    Product()

