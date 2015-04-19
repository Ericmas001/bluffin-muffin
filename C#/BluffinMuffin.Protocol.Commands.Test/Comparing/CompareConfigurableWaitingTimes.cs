﻿using BluffinMuffin.Poker.DataTypes.Parameters;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace BluffinMuffin.Protocol.Commands.Test.Comparing
{
    public static class CompareConfigurableWaitingTimes
    {
        public static void Compare(ConfigurableWaitingTimes c, ConfigurableWaitingTimes dc)
        {
            Assert.AreEqual(c.GetType(), dc.GetType());
            Assert.AreEqual(c.AfterBoardDealed, dc.AfterBoardDealed);
            Assert.AreEqual(c.AfterPlayerAction, dc.AfterPlayerAction);
            Assert.AreEqual(c.AfterPotWon, dc.AfterPotWon);
        }
    }
}
