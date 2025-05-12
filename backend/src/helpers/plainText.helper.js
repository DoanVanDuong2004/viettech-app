const striptags = require('striptags');
const sanitizeHtml = require('sanitize-html');

const he = require('he'); 

function htmlToPlainText(html) {
    if (!html) return "";
    const plainText = striptags(html)
        .replace(/\n\s*\n/g, '\n') // loại bỏ dòng trống thừa
        .trim(); // cắt trắng đầu cuối
    return he.decode(plainText); // Giải mã HTML entities
}

function cleanHtmlContent(html) {
    return sanitizeHtml(html, {
        allowedTags: sanitizeHtml.defaults.allowedTags.concat(['img', 'h1', 'h2', 'u', 's']),
        allowedAttributes: {
            a: ['href', 'name', 'target'],
            img: ['src', 'alt']
        },
        transformTags: {
            '*': (tagName, attribs) => {
                const cleanAttribs = {};
                for (const key in attribs) {
                    if (!key.startsWith('data-') && key !== 'class') {
                        cleanAttribs[key] = attribs[key];
                    }
                }
                return { tagName, attribs: cleanAttribs };
            }
        }
    });
}


module.exports = { htmlToPlainText, cleanHtmlContent };
