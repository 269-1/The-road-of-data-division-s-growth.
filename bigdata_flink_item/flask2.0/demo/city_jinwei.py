import time
import requests
from lxml import etree
headers = {
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3947.100 Safari/537.36 2345Explorer/10.11.0.20694'}
#
jinwei = {}  # 用于存放各大城市的主要经纬度

def get_jinwei():
    url = 'https://www.d1xz.net/xp/jingwei/'
    time.sleep(1)
    ans = requests.get(url=url, headers=headers)
    end = ans.text
    Html = etree.HTML(end)
    for i in range(1, 5):
        res = Html.xpath('//div[@class="inner_con_art"]/table//tr[' + str(i) + ']/td')
        for j in res:
            res_end = j.xpath('./strong/a/@href')
            if len(res_end) != 0:
                url_end = 'https://www.d1xz.net/' + res_end[0]
                ans_1 = requests.get(url=url_end, headers=headers)
                end_1 = ans_1.text
                Html_1 = etree.HTML(end_1)
                res_1 = Html_1.xpath('//div[@class="inner_con_art"]/table//tr')
                for num_ in range(2, len(res_1)):
                    end_end = Html_1.xpath('//div[@class="inner_con_art"]/table//tr[' + str(num_) + ']/td/text()')
                    jinwei.update({end_end[0]: [float(end_end[1]), float(end_end[2])]})
    jinwei.update({'香港': [114.12, 22.26]})
    import pandas as pd
    data2 = pd.read_csv('jinwei.csv', encoding='gbk')
    data=list(zip(list(data2['经度']), list(data2['纬度'])))
    for i in range(len(data)):
        data[i]=list(data[i])
    data3 = dict(zip(list(data2['地址']), data))
    jinwei.update(data3)
get_jinwei()
with open("jinwei.text",mode='w',encoding='utf-8') as f:
    f.write(str(jinwei))
