# Series-Monitor

I needed to learn Java for my university, so I decided to build a small gui-application for practical experience.

The GUI of this tool is made with JavaFX. I did all the styles via [Scene-Builder](http://gluonhq.com/products/scene-builder/) because it seemed to be the easiest solution since I am not really a good designer.

Another big extension I used is [Selenium Webdriver](https://www.seleniumhq.org/projects/webdriver/). It is really helpful for scraping the internet.

For my purposes it's really 'ugly' if a browser pops up everytime you press 'Refresh' to see if another Episode of your Series are out. Therefore I decided to use a headless browser called [HTML-UnitDriver](https://github.com/SeleniumHQ/htmlunit-driver).

With it's help it was kinda easy to check for updates in the background so the user won't notice any of this work.

To store the user progress when closing the application I used a simple json-file-database. Due it's simplicity this fits perfect into my little project.

## Screenshots

| home                                     | updating                                         |
| ---------------------------------------- | ------------------------------------------------ |
| ![home](assets/home.png) | ![updating](assets/updating.png) |


| series in detail |
| --- |
| ![series](assets/series.png) |

| database structure |
| --- |
| ![database](assets/database.png) |
