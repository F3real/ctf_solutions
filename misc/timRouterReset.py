from splinter import Browser

# Requires geckodriver.exe
with Browser('firefox') as browser:
    browser.visit('http://192.168.1.1/login.html')
    browser.find_by_value('Password').fill('admin')
    browser.click_link_by_id('start')
    browser.is_element_not_present_by_xpath('xxxx', 10) #using this as pause
    print 'Logged in admin panel'
    browser.click_link_by_id('lang700036')
    print 'Navigated to settings'
    browser.is_element_not_present_by_xpath('xxxx', 10) #using this as pause
    browser.click_link_by_id('lang906007')
    print 'Starting restart'
    browser.is_element_not_present_by_xpath('xxxx', 10) #using this as pause
    browser.click_link_by_id('restartB')
    browser.quit()