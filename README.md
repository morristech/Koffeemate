# Koffeemate
<img src="https://github.com/roughike/Koffeemate/blob/master/art/screenshot-coffee-incoming.png?raw=true" />

# What?
We at Codemate **love** coffee. Numerous cups of that sweet black nectar are brewed every day. Coffee is what keeps us productive, creative and especially on Mondays, awake. Simply put, we just couldn't function without it.

# Okay, but still, what?
Koffeemate was made for three purposes:

1. Informing others on Slack when freshly brewed coffee is available
2. Gathering interesting data of our coffee consumption
3. Publicly shaming those who leave a giant mess behind while they try to brew coffee.

# How does it work?
The system is very elegant: we have a cheap Android phone glued to the wall next to our coffee machine. Running this app is the only thing that phone can do. We made this extra secure by taping some cardboard over the physical buttons.

Everytime someone starts the coffee machine, they also press the coffee pot button on the center of the screen. After exactly 7 minutes, which is the most appropriate delay we've found, everyone in the special Slack channel gets notified. 

However, if someone fails the coffee brewing process, they can be publicly shamed by using the "Log an accident" button.

# That's neat! We want it too!
Of course you do. Here's the steps to get it working:

## Create a bot user on Slack
1. Go to [the custom integrations page on Slack](https://api.slack.com/custom-integrations), and click the ```Create a bot user``` button.
2. Click the green ```Add Configuration``` button.
3. Choose a username for your bot and click ```Add bot integration```.
4. Configure your bot the way you like. **Take note of the API token, you'll need it next.**
5. **IMPORTANT:** Invite the newly-made bot to any channels you would like the coffee announcements to be made on.

## Make it work with Koffeemate
1. Change to a folder of your liking and do a ```git clone https://github.com/roughike/Koffeemate.git```
2. **Don't open the project yet.**
3. Create an empty ```gradle.properties``` file in the **project root** with the following contents:

**Koffeemate/gradle.properties:**
```groovy
SLACK_AUTH_TOKEN = "YOUR_SLACK_BOT_API_TOKEN" // Replace with the actual token
SLACK_BOT_NAME = "YOUR_SLACK_BOT_NAME" // Whatever you like your bot's name to be.
```

Now you can open the project in Android Studio. 

Install the app to an old phone, glue it to a wall near a coffee machine and enjoy!

# License

```
Copyright 2016 Codemate Ltd

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```