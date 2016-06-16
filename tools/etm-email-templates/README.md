# Foundation for Emails Template

[![devDependency Status](https://david-dm.org/zurb/foundation-emails-template/dev-status.svg)](https://david-dm.org/zurb/foundation-emails-template#info=devDependencies)

**Please open all issues with this template on the main [Foundation for Emails](http://github.com/zurb/foundation-emails/issues) repo.**

This is the official starter project for [Foundation for Emails](http://foundation.zurb.com/emails), a framework for creating responsive HTML devices that work in any email client. It has a Gulp-powered build system with these features:

- Handlebars HTML templates with [Panini](http://github.com/zurb/panini)
- Simplified HTML email syntax with [Inky](http://github.com/zurb/inky)
- Sass compilation
- Image compression
- Built-in BrowserSync server
- Full email inlining process

## Installation

If you followed [ETM developer setup](https://github.com/Perficient/ent-talent-mgmt/wiki/Developer-Setup) , you should have node installed.

If you only want to work on email templates, [install Node version 4.2.4](https://nodejs.org/download/release/v4.2.4/) (tested).

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
├── dist (this is where distribution files will be after building with build commands above)
├── etc (empty)
└── src (source pages, partials and assets)
    ├── assets (SCSS and images)
    ├── layouts
        └── default.html (the main layout for html emails) 
    ├── pages (email's main content)
    └── partials (email html fragments like header, footer ..etc)

