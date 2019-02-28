# Hot functions

Hot functions improve performance by starting a function then keeping it alive to handle additional requests. This eliminates the high cost of starting new containers, but makes it a bit trickier to use because you'll have to parse a stream of requests. If you you use an [FDK](fdks.md) than this is all taken care of for you. In most cases, you should probably use an FDK.
