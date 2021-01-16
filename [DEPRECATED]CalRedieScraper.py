from selenium import webdriver
import re
import os

driver = webdriver.Chrome("chromedriver")
driver.get('https://data.ca.gov/dataset/590188d5-8545-4c93-a9a0-e230f0db7290/resource/926fd08f-cc91-4828-af38-bd45de97f8c3/download/statewide_cases.csv')

file_address = "statewide_cases.csv"

sc_new_cases = []

