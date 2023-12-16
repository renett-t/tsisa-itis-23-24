Где проверить: http://itislabs.ru/currency/bc

{"name": "ФИО студента, номер группы",

"currency1": "Код валюты 1",

"currency2": "Код валюты 2",

"currency3": "Код валюты 3",

"currency4": "Код валюты 4",

"strategy": "Код стратегии",

"x11":" … ",

… ,

"x44":" … ",

"p11":" … ",

… ,

"p44":" … "

}


Отправка на сервер информации о выборе валют и стратегии:

http://itislabs.ru/currency?value=%7B"name":"Тяпкина%20Регина%20Геннадьевна,%2011-001","currency1":"AUD","currency2":"SGD","currency3":"GBP","currency4":"HUF","strategy":"s3"%7D

Параметр value — URLEncoded json-объект {"name":"Еникеев Камиль Шамилевич","currency1":"AUD","currency2":"SGD","currency3":"GBP","currency4":"HUF","strategy":"s3"}
Допустимый перечень валют:  AUD, CAD, EUR, USD, SGD, JPY, DKK, CHF, GBP, NOK, CNY, HUF, TRY, INR, IDR
Допустимый перечень стратегий: S1, S2, S3, S4
https://github.com/ksenikeev/sysanalysis/blob/master/tspserver/src/main/java/ru/itis/sa/arbiter/gametheory/TestSender.java