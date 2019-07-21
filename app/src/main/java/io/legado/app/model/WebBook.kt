package io.legado.app.model

import io.legado.app.data.entities.Book
import io.legado.app.data.entities.BookChapter
import io.legado.app.data.entities.BookSource
import io.legado.app.data.entities.SearchBook
import io.legado.app.help.coroutine.Coroutine
import io.legado.app.model.analyzeRule.AnalyzeUrl
import io.legado.app.model.webbook.BookChapterList
import io.legado.app.model.webbook.BookInfo
import io.legado.app.model.webbook.BookList

class WebBook(private val bookSource: BookSource) {

    fun searchBook(key: String, page: Int?): Coroutine<List<SearchBook>> {
        return Coroutine.async {
            bookSource.getSearchRule().searchUrl?.let { searchUrl ->
                val analyzeUrl = AnalyzeUrl(searchUrl, key, page)
                val response = analyzeUrl.getResponseAsync().await()
                return@async BookList.analyzeBookList(response, bookSource, analyzeUrl)
            }
            return@async arrayListOf<SearchBook>()
        }
    }

    fun findBook(key: String, page: Int?): Coroutine<List<SearchBook>> {
        return Coroutine.async {
            bookSource.getSearchRule().searchUrl?.let { searchUrl ->
                val analyzeUrl = AnalyzeUrl(searchUrl, key, page)
                val response = analyzeUrl.getResponseAsync().await()
                return@async BookList.analyzeBookList(response, bookSource, analyzeUrl, false)
            }
            return@async arrayListOf<SearchBook>()
        }
    }

    fun getBookInfo(book: Book): Coroutine<Book> {
        return Coroutine.async {
            val analyzeUrl = AnalyzeUrl(book = book, ruleUrl = book.bookUrl)
            val response = analyzeUrl.getResponseAsync().await()
            BookInfo.analyzeBookInfo(book, response.body(), bookSource, analyzeUrl)
            return@async book
        }
    }

    fun getChapterList(book: Book, success: (List<BookChapter>) -> Unit) {
        Coroutine.async {
            val analyzeUrl = AnalyzeUrl(book = book, ruleUrl = book.tocUrl)
            val response = analyzeUrl.getResponseAsync().await()
            BookChapterList.analyzeChapterList(book, response, bookSource, analyzeUrl, success)
        }
    }
}