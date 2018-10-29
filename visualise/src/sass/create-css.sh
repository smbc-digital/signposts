#!/bin/sh

sass site.scss site.css
git add site.scss site.css
cp site.css ../../resources/public/css/site.css
