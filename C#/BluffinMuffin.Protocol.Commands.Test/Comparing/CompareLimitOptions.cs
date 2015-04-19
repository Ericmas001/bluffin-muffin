﻿using BluffinMuffin.Poker.DataTypes.Parameters;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace BluffinMuffin.Protocol.Commands.Test.Comparing
{
    public static class CompareLimitOptions
    {
        public static void Compare(LimitOptions l, LimitOptions dl)
        {
            Assert.AreEqual(l.GetType(), dl.GetType());
        }
    }
}
