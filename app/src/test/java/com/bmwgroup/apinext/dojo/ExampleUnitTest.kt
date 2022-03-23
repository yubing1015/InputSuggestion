package com.bmwgroup.apinext.dojo

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun `GIVEN 天 WHEN have word start with 天 THEN return the word 安 in list`() {
        val input = "天"

        val words = KeyBoardSuggestion().getSuggestionWords(input)

        assert(words.contains("安"))
    }

    @Test
    fun `GIVEN 天 WHEN have word start with 天 THEN return a word list`() {
        val input = "天"

        val words = KeyBoardSuggestion().getSuggestionWords(input)

        assert(words.contains("安") && words.contains("坛"))
    }

    @Test
    fun `GIVEN 【天安门，天坛】 WHEN have word start with 天安 THEN return 门`() {
        val input = "天安"

        val words = KeyBoardSuggestion().getSuggestionWords(input)

        assert(words.contains("门"))
    }

    @Test
    fun `GIVEN 【天安门，天坛, 天安门西， 天安门东】 WHEN have word start with 天安门 THEN return 【东，西】`() {
        val input = "天安门"

        val words = KeyBoardSuggestion().getSuggestionWords(input)

        assert(words.contains("东") && words.contains("西"))
    }

    @Test
    fun `GIVEN 【天安门，天坛, 天安门西， 天安门东，天安大厦】 WHEN have word start with 天安大厦 THEN return nothing`() {
        val input = "天安大厦"

        val words = KeyBoardSuggestion().getSuggestionWords(input)

        assert(words.isEmpty())
    }

    @Test
    fun `GIVEN addr list WHEN  hashmap created  THEN get TrieNode `() {
        val addrList: List<String> = listOf()

        val words = KeyBoardSuggestion().createTrie(addrList)

        assertTrue(words.children.isEmpty())
    }

    @Test
    fun `GIVEN addr list is "天"， WHEN  hashmap created  THEN get TrieNode key is "天"`() {
        val addrList: List<String> = listOf("天")

        val words = KeyBoardSuggestion().createTrie(addrList)

        assertTrue(words.children.keys.containsAll(listOf('天')))
    }

    @Test
    fun `GIVEN addr list is "天安"， WHEN  hashmap created  THEN get child TrieNode key is "安"`() {
        val addrList: List<String> = listOf("天安")

        val words = KeyBoardSuggestion().createTrie(addrList)

        assertTrue(words.children.keys.contains('天'))
    }

    @Test
    fun `GIVEN address list is "天坛", WHEN hashmap created THEN get child TrieNode key is "坛"`() {
        val addressList : List<String> = listOf("天安", "天坛")

        val words = KeyBoardSuggestion().createTrie(addressList)

        assertTrue(words.children['天']?.children!!.keys.contains('安'))
        assertTrue(words.children['天']?.children!!.keys.contains('坛'))
    }

    @Test
    fun `GIVEN address list is "天安门", WHEN hashmap created THEN get child TrieNode key is "门"`() {
        val addressList : List<String> = listOf("天安门", "天坛")

        val words = KeyBoardSuggestion().createTrie(addressList)

        assertTrue(words.children['天']!!.children['安']!!.children.keys.contains('门'))
    }

    @Test
    fun `GIVEN address list is "天天安", "天天坛", WHEN hashmap created THEN the second level child size == 1 `() {
        val addressList : List<String> = listOf("天天安", "天天坛")

        val words = KeyBoardSuggestion().createTrie(addressList)

        assertTrue(words.children['天']!!.children.size == 1)
    }

    @Test
    fun `GIVEN address list is "天天安", "大天坛", WHEN hashmap created THEN the first level child size == 2 `() {
        val addressList : List<String> = listOf("天天安", "大天坛")

        val words = KeyBoardSuggestion().createTrie(addressList)

        assertTrue(words.children.size == 2)
    }

    @Test
    fun `GIVEN input is "天安s", WHEN search THEN search result is empty`() {
        val input = "天安s"

        val result = KeyBoardSuggestion().getSuggestionWords(input)

        assertTrue(result.isEmpty())
    }

    class KeyBoardSuggestion {
        private val dictionary: TrieNode = createTrie(AddressSuggestionDictionary.addressList)

        fun getSuggestionWords(input: String): List<String> {
            var nodes: HashMap<Char, TrieNode?>? = dictionary.children
            for(i in input.indices) {
               nodes = nodes?.getOrDefault(input[i], null)?.children
            }
            return nodes?.map { it.key.toString() } ?: emptyList()
        }

        class TrieNode(val value: Char?, var children: HashMap<Char, TrieNode?>)

        fun createTrie(list: List<String>): TrieNode {
            val root = TrieNode(null, hashMapOf())
            list.forEach {
                var curNode = root
                it.forEach {
                    var nextNode = curNode.children[it]
                    if (nextNode == null) {
                        nextNode = TrieNode(it, hashMapOf())
                        curNode.children[it] = nextNode
                    }
                    curNode = curNode.children[it]!!
                }
            }
            return root
        }
    }

    object AddressSuggestionDictionary {
        val addressList = listOf("天安门", "天坛", "天安门东", "天安门西", "天安大厦", "King KTV", "顺义")
    }
}

