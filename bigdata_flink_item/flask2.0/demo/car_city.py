import requests
from lxml import etree
import json
result={}
def begin():
    global result
    url="http://www.tcmap.com.cn/list/car_list.html"
    headers={
        "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Safari/537.36"
    }
    response=requests.get(url=url,headers=headers)
    response.encoding='gbk'
    response_text=response.text
    ans=etree.HTML(response_text)
    result1=ans.xpath('//div[@id="page_left"]/div')
    res={}
    for i in result1[:-1]:
        mid=i.xpath('./table//tr')
        for j in mid[1:]:
            txt=j.xpath('./td[1]/a/text()')[0]
            txt1=j.xpath('./td[2]/text()')[0]
            res.update({str(txt1).strip():str(txt)})
    #特殊城市
    res.update({'沪E':'黄浦区'})
    # result.update({'渝D':'渝中区'})
    res.update({'粤I':'惠州市'})
    res.update({'粤E':'佛山市'})
    res.update({'粤X':'佛山市'})
    res.update({'粤Z':'中西区'})
    for i in res.keys():
        if i not in result.keys():
            result.update({i:res[i]})
    with open ('sult.text',encoding='utf-8',mode='w') as f:
        f.write(str(result))
def begin_other():
    global result
    dict=[]
    with open('city.json',mode='r',encoding='utf-8') as f:
        dict=eval(f.read())
    for i in dict:
        result.update({str(i['code']).strip():str(i['city']).strip()})
    result.update({'沪E':'上海'})
begin_other()
begin()