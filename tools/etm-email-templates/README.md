# Foundation for Emails Template

[![devDependency Status](https://david-dm.org/zurb/foundation-emails-template/dev-status.svg)](https://david-dm.org/zurb/foundation-emails-template#info=devDependencies)


This project is built from the official project [Foundation for Emails](http://foundation.zurb.com/emails), a framework for creating responsive HTML devices that work in any email client. It has a Gulp-powered build system with these features:

- Handlebars HTML templates with [Panini](http://github.com/zurb/panini)
- Simplified HTML email syntax with [Inky](http://github.com/zurb/inky)
- Sass compilation
- Image compression
- Built-in BrowserSync server
- Full email inlining process

## Installation

If you followed [ETM developer setup](https://github.com/Perficient/ent-talent-mgmt/wiki/Developer-Setup) , you should have node installed.

If you only want to work on email templates, [install Node version 6.10.0](https://nodejs.org/download/release/v6.10.0/) (tested).

### Using the CLI

Install the Foundation CLI with this command:

```bash
npm install foundation-cli --global
```

## Build Commands

Run `npm start` to kick off the build process. A new browser tab will open with a server pointing to your project files.

Run `npm run build` to inline your CSS into your HTML along with the rest of the build process.

## Project structure
    etm-email-templates
    ├── dist                    # Where dist files will be after building with build commands above
    ├── etc                     # Empty
    └── src                     # source pages, partials and assets
        ├── assets              # SCSS and images
        ├── layouts             # 
            └── default.html    # Main layout for html emails
        ├── pages               # Html email files (main content)
        └── partials            # Email html fragments like header, footer ..etc
        
## Thymeleaf
We are using Thymeleaf 1.2 to inject variables and do some logic for emails before we send them. [Thymeleaf docs can be found here](http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html#dialects-the-standard-dialect).
if you take a look at `com.perficient.etm.service.EmailConstants` you'll find standard variables that you can use in the email templates, those variables are injected into thymeleaf template processor in the mail service class `com.perficient.etm.service.MailService`. meaning they will be available for every email template:

**Current user:** information about the currently loged in user (also know as the email sender)

`CURRENT_USER_FIRST_NAME`, `CURRENT_USER_LAST_NAME`, `CURRENT_USER_FULL_NAME`, `CURRENT_USER_ID`, `CURRENT_USER_EMAIL`, `CURRENT_USER_LOGIN`, `CURRENT_USER_TITLE`, `CURRENT_USER_TARGET_TITLE`

**Recipient user:** user recieving this email

 `FIRST_NAME`, `LAST_NAME`, `FULL_NAME`, `ID`, `EMAIL`, `LOGIN`, `TITLE`, `TARGET_TITLE`

**Review:** the review about which this email is sent

`REVIEW_TYPE`, `REVIEW_ID`, `REVIEW_STATUS`, `REVIEWEE_FIRST_NAME`, `REVIEWEE_LAST_NAME`, `REVIEWEE_FULL_NAME`, `REVIEWEE_ID`, `REVIEWER_FIRST_NAME`, `REVIEWER_LAST_NAME`, `REVIEWER_FULL_NAME`, `REVIEWER_ID`

**Others**

`BASE_URL` is the domain+port for the web app
`PROFILE` application profile (dev, test, uat, prod)


