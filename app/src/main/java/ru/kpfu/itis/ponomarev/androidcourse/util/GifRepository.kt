package ru.kpfu.itis.ponomarev.androidcourse.util

import ru.kpfu.itis.ponomarev.androidcourse.model.GifButtonModel
import ru.kpfu.itis.ponomarev.androidcourse.model.GifCardModel
import ru.kpfu.itis.ponomarev.androidcourse.model.GifDateModel
import ru.kpfu.itis.ponomarev.androidcourse.model.GifModel
import java.util.Calendar
import kotlin.random.Random

object GifRepository {
    private val feedList = mutableListOf<GifModel>()

    private val gifCardList: List<GifCardModel>

    init {
        gifCardList = listOf(
            GifCardModel(
                1,
                "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExOTgza3ExbTVueXpqOTB2cnlnN3lmc21udmdzMXZpd3hzbmU1bnRmYyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/TGHXd9J6mK6sM/giphy.gif",
                "Halloween Skeleton GIF",
                listOf(
                    "#halloween",
                    "#scary",
                    "#spooky",
                    "#skeleton",
                    "#scary_gif"
                ),
            ),
            GifCardModel(
                2,
                "https://media.giphy.com/media/3o7qDPxorBbvpB1Pby/giphy.gif",
                "animation bubble GIF by Chris Gannon",
                listOf(
                    "#animation",
                    "#bubble",
                    "#filter",
                    "#liquid",
                    "#blob",
                    "#javascript",
                    "#goo",
                    "#svg",
                    "#codepen",
                    "#gsap",
                ),
            ),
            GifCardModel(
                3,
                "https://media.giphy.com/media/xT39DgKMixPKDrwzf2/giphy.gif",
                "abstract flux GIF by Pi-Slices",
                listOf(
                    "#trippy",
                    "#abstract",
                    "#pi-slices",
                    "#flux",
                    "#fluctuating",
                    "#fluctuate",
                ),
            ),
            GifCardModel(
                4,
                "https://media.giphy.com/media/2dJyxamuK19B91nJUP/giphy.gif",
                "Emote Give Sticker by Twitch",
                listOf(
                    "#cute",
                    "#twitch",
                    "#emote",
                    "#mine",
                    "#give",
                    "#gimme",
                    "#emotes",
                    "#giveme",
                    "#twitch_emotes",
                    "#giveplz",
                ),
            ),
            GifCardModel(
                5,
                "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExNnB3d2Z6ZjUwd284NXBvZjl4bnN4bWZ4dmNzMmpwN3ZwZHNjMDJjcSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/udbOtWrW0QDaoX2fyg/giphy.gif",
                "Ome5 GIF",
                listOf(
                    "#ome5",
                ),
            ),
            GifCardModel(
                6,
                "https://media.giphy.com/media/nR4L10XlJcSeQ/giphy.gif",
                "No Way Cat GIF",
                listOf(
                    "#reaction",
                    "#cat",
                    "#cute",
                    "#no",
                    "#nope",
                    "#nsfw",
                    "#no_way",
                    "#nein",
                    "#ani",
                    "#not_safe_for_work",
                    "#아니",
                    "#cat_no",
                ),
            ),
            GifCardModel(
                7,
                "https://media.giphy.com/media/vFKqnCdLPNOKc/giphy.gif",
                "White Cat Hello GIF",
                listOf(
                    "#cat",
                    "#hello",
                    "#white",
                    "#like",
                    "#sweet",
                    "#hug",
                    "#hey",
                    "#bored",
                    "#cute_cat",
                    "#paws",
                    "#hai",
                    "#hay",
                    "#cat_gif",
                    "#reazioni",
                    "#white_cat",
                ),
            ),
            GifCardModel(
                8,
                "https://media.giphy.com/media/B7ppUExX92PjW/giphy.gif",
                "Cat Waterfall GIF",
                listOf(
                    "#cat",
                    "#wet",
                    "#waterfall",
                    "#sink",
                    "#wap",
                ),
            ),
            GifCardModel(
                9,
                "https://media.giphy.com/media/11IYKJ5sN73twk/giphy.gif",
                "Cat GIF",
                listOf(
                    "#funny",
                    "#cat",
                    "#cute",
                    "#laser",
                ),
            ),
            GifCardModel(
                10,
                "https://media.giphy.com/media/VCma6o7KQ4H4Y/giphy.gif",
                "Relax Reaction GIF by VICE España",
                listOf(
                    "#reaction",
                    "#cute",
                    "#relax",
                    "#cozy",
                    "#warm",
                    "#vice",
                    "#comfy",
                    "#capybara",
                ),
            ),
            GifCardModel(
                11,
                "https://media.giphy.com/media/xUNda7l1mkblth6siQ/giphy.gif",
                "savage kingdom big cat week GIF by Nat Geo Wild",
                listOf(
                    "#nat_geo_wild",
                    "#capybara",
                    "#savage_kingdom",
                    "#big_cat_week",
                    "#capybaras",
                    "#jaguar_vs_croc",
                ),
            ),
            GifCardModel(
                12,
                "https://media.giphy.com/media/3yUpt6HCRKMvHocy7x/giphy.gif",
                "Happy Loop GIF",
                listOf(
                    "#happy",
                    "#cartoon",
                    "#loop",
                    "#animals",
                    "#capybara",
                    "#capy",
                    "#nivoraanimation",
                    "#capy_spinning",
                ),
            ),
            GifCardModel(
                13,
                "https://media.tenor.com/SDwGg31pp4AAAAAC/maxwell-the-cat-maxwell.gif",
                "Maxwell The Cat Cat GIF",
                listOf(
                    "#maxwell_the_cat",
                    "#maxwell",
                    "#cat",
                    "#polygon_donut",
                    "#pongon",
                    "#gen_z_cat_memes",
                    "#gen_z",
                ),
            )
        )
    }

    fun addRandomCards(n: Int) {
        if (n != 0) {
            if (feedList.size == 1) { // only button
                feedList.add(GifDateModel(Calendar.getInstance().time))
            }
            for (i in 1..n) {
                val index = Random.nextInt(2, feedList.size + 1)
                feedList.add(index, gifCardList.random().copy(id = feedList.size + 1))
            }
        }
    }

    fun initFeedList(n: Int) {
        feedList.clear()
        feedList.add(GifButtonModel)
        for (i in 0 until n) { // if n == 0 this loop is skipped and no dates would be added
            if (i % 8 == 0) {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -(i / 8)) // subtract (i / 8) days from today
                feedList.add(GifDateModel(calendar.time))
            }
            feedList.add(gifCardList.random().copy(id = feedList.size + 1))
        }
    }

    fun setItem(position: Int, gif: GifModel) {
        feedList[position] = gif
    }

    fun removeItem(position: Int) {
        feedList.removeAt(position)
    }

    fun addItem(position: Int, gif: GifModel) {
        feedList.add(position, gif)
    }

    fun clear() {
        feedList.clear()
    }

    fun getFeedList(): List<GifModel> = feedList
}
