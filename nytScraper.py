from selenium import webdriver
import re
import os

driver = webdriver.Chrome('Chrome/chromedriver')
driver.get('https://raw.githubusercontent.com/nytimes/covid-19-data/master/us-states.csv')

content = driver.page_source
content = re.sub('<.*>', '', content)
#content = re.findall('>(.+)<', content)

fh = open('src/sourceData/us-states.csv', 'w')

fh.write(content)


print(os.getcwd())

os.system("cd " + os.getcwd().replace(" ", "\ ") + "/src \n javac *.java \n java Main")

print("Done!")