// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[dynamodb.dotNET.CodeExample.00b_DDB_Attributes]
using System;
using System.Text;
using System.Collections.Generic;
using Amazon.DynamoDBv2.Model;
using Amazon.DynamoDBv2.DocumentModel;

namespace DynamoDB_intro
{
  public static partial class DdbIntro
  {
    public const string ListStart = "{0}\n{1}\"{2}\": [";
    public const string ListEnd   = "\n{0}]";
    public const string ObjStart  = "{0}\n{1}\"{2}\": {{";
    public const string ObjEnd    = "\n{0}}";
    public const string KvNum    = "{0}\n{1}\"{2}\": {3}";
    public const string KvStr    = "{0}\n{1}\"{2}\": \"{3}\"";
    public const string ValStr   = "{0}\n{1}  \"{2}\"";
    static string _shortFormatStrA = "          {0}:  \"{1}\"\n" +
                                     "                  -- lead: {2}\n" +
                                     "                  -- genres: {3}\n" +
                                     "                  -- running time: {4}";
    static string _shortFormatStrB = "          {0}:  \"{1}\"\n" +
                                     "                  -- lead: {2}\n" +
                                     "                  -- director(s): {3}\n" +
                                     "                  -- running time: {4}";

    public static void showDocument(Document doc)
    {
            /*
             * The new movie:
             *
             *  ["year"] = year,
                    ["title"] = title,
                    ["info"] = Document.FromJson(
                        "{\"plot\" : \"Nothing happens at all.\",\"rating\" : 0}")
             */
            var year = doc["year"];
            if (null != year) Console.WriteLine("Year:  " + year);
            var title = doc["title"];
            if (null != year) Console.WriteLine("Title: " + title);
    }

    public static void showMovieAttrsShort( Dictionary<string, AttributeValue> movie )
    {
      Dictionary<string, AttributeValue> info = movie["info"].M;
      string rtm;
      if( info.ContainsKey( "running_time_secs" ) )
      {
        int rsecs;
        if( int.TryParse( info["running_time_secs"].N, out rsecs ) )
          rtm = movieRunTime( rsecs );
        else
          rtm = "?";
      }
      else
        rtm = "?";
      if( info.ContainsKey( "genres") )
        Console.WriteLine( _shortFormatStrA, movie["year"].N, movie["title"].S,
                           firstAttrValToString( info["actors"] ),
                           attrValToString( info["genres"], 0 ), rtm );
      else if( info.ContainsKey( "directors") )
        Console.WriteLine( _shortFormatStrB, movie["year"].N, movie["title"].S,
                           firstAttrValToString( info["actors"] ),
                           attrValToString( info["directors"], 0 ), rtm );
    }

    public static void showMovieDocShort( Document movie )
    {
      Document infoDoc = movie["info"].AsDocument( );
      string rtm;
      if( infoDoc.ContainsKey( "running_time_secs" ) )
      {
        int rsecs;
        if( int.TryParse( infoDoc["running_time_secs"], out rsecs ) )
          rtm = movieRunTime( rsecs );
        else
          rtm = "?";
      }
      else
        rtm = "?";
      if( infoDoc.ContainsKey( "genres") )
        Console.WriteLine( _shortFormatStrA, movie["year"], movie["title"],
                           infoDoc["actors"].AsArrayOfString( )[0],
                           string.Join( CommaSep, infoDoc["genres"].AsArrayOfString( ) ), rtm );
      else if( infoDoc.ContainsKey( "directors" ) )
        Console.WriteLine( _shortFormatStrB, movie["year"], movie["title"],
                           infoDoc["actors"].AsArrayOfString( )[0],
                           string.Join( CommaSep, infoDoc["directors"].AsArrayOfString( ) ), rtm );
    }

    public static string movieAttributesToJson( Dictionary<string, AttributeValue> movie )
    {
      StringBuilder sb = new StringBuilder();
      string next = "";

      sb.Append( "{" );
      sb.Append( string.Format( KvNum, "", "  ", "year", movie["year"].N ) );
      sb.Append( string.Format( KvStr, ",", "  ", "title", movie["title"].S ) );
      if( movie.ContainsKey( "info" ) )
      {
        Dictionary<string, AttributeValue> info = movie["info"].M;
        sb.Append( string.Format( ObjStart, ",", "  ", "info" ) );
        if( info.ContainsKey( "plot" ) )
        {
          sb.Append( string.Format( KvStr, next, "    ", "plot", info["plot"].S ) );
          next = ",";
        }
        if( info.ContainsKey( "running_time_secs" ) )
        {
          int rsecs;
          if( int.TryParse( info["running_time_secs"].N, out rsecs ) )
          {
            sb.Append( string.Format( KvStr, next, "    ", "run-time",
                                      movieRunTime( rsecs ) ) );
            next = ",";
          }
        }
        if( info.ContainsKey( "rating" ) )
        {
          sb.Append( string.Format( KvNum, next, "    ", "rating", info["rating"].N ) );
          next = ",";
        }
        if( info.ContainsKey( "directors" ) )
        {
          sb.Append( string.Format( ListStart, next, "    ", "directors" ) );
          sb.Append( attrValToLines( movie["directors"], 0,  "      " ) );
          sb.Append( string.Format( ListEnd, "    " ) );
          next = ",";
        }
        if( info.ContainsKey( "genres" ) )
        {
          sb.Append( string.Format( ListStart, next, "    ", "genres" ) );
          sb.Append( attrValToLines( movie["genres"], 0, "      " ) );
          sb.Append( string.Format( ListEnd, "    " ) );
          next = ",";
        }
        if( info.ContainsKey( "actors" ) )
        {
          sb.Append( string.Format( KvStr, next, "    ", "lead",
                                    firstAttrValToString( info["actors"] ) ) );
          next = ",";
          if( attrValLength( info["actors"] ) > 1 )
          {
            sb.Append( string.Format( ListStart, next, "    ", "actors" ) );
            sb.Append( attrValToLines( info["actors"], 1, "      " ) );
            sb.Append( string.Format( ListEnd, "    " ) );
          }
        }
      }
      sb.Append( "\n}" );
      return ( sb.ToString( ) );
    }

    public static string attrValToString( AttributeValue attrVal, int startIndex )
    {
      string str = null;

      if( attrVal.S != null )
        str = attrVal.S;
      else if( attrVal.N != null )
        str = attrVal.N;
      else if( attrVal.SS.Count > 0 )
      {
        string[] strs = attrVal.SS.ToArray( );
        str = string.Join( CommaSep, strs, startIndex, strs.Length - startIndex );
      }
      else if( attrVal.NS.Count > 0 )
      {
        string[] strs = attrVal.NS.ToArray( );
        str = string.Join( CommaSep, strs, startIndex, strs.Length - startIndex );
      }
      else if( attrVal.L.Count > 0 )
        str = attrListToString( attrVal.L, startIndex );
      return ( str );
    }

    public static string firstAttrValToString( AttributeValue attrVal )
    {
      string str = null;

      if( attrVal.S != null )
        str = attrVal.S;
      else if( attrVal.N != null )
        str = attrVal.N;
      else if( attrVal.SS.Count > 0 )
        str = attrVal.SS[0];
      else if( attrVal.NS.Count > 0 )
        str = attrVal.NS[0];
      else if( attrVal.L.Count > 0 )
        str = attrValToString( attrVal.L[0], 0 );
      return ( str );
    }

    public static string attrListToString( List<AttributeValue> attrList, int startIndex )
    {
      StringBuilder sb = new StringBuilder();
      string str;

      for( int i = startIndex; i < attrList.Count; i++ )
      {
        str = attrValToString( attrList[i], 0 );
        if( str != null )
        {
          if( i > 0 )
            sb.Append( CommaSep );
          sb.Append( str );
        }
      }
      return ( sb.ToString( ) );
    }

    public static string attrValToLines( AttributeValue attrVal, int startIndex, string indent )
    {
      string next = "";
      if( attrVal.S != null )
      {
        if( startIndex == 0 )
          return ( string.Format( ValStr, next, indent, attrVal.S ) );
      }
      else if( attrVal.N != null )
      {
        if( startIndex == 0 )
          return ( string.Format( ValStr, next, indent, attrVal.N ) );
      }
      else
      {
        StringBuilder sb = new StringBuilder();

        if( attrVal.SS.Count > 0 )
        {
          if( attrVal.SS.Count > startIndex )
          {
            string[] strs = attrVal.SS.ToArray( );
            for( int i = startIndex; i < strs.Length; i++ )
            {
              sb.Append( string.Format( ValStr, next, indent, strs[i] ) );
              next = ",";
            }
          }
        }
        else if( attrVal.NS.Count > 0 )
        {
          if( attrVal.NS.Count > startIndex )
          {
            string[] strs = attrVal.NS.ToArray( );
            for( int i = startIndex; i < strs.Length; i++ )
            {
              sb.Append( string.Format( ValStr, next, indent, strs[i] ) );
              next = ",";
            }
          }
        }
        else if( attrVal.L.Count > 0 )
        {
          if( attrVal.L.Count > startIndex )
          {
            for( int i = startIndex; i < attrVal.L.Count; i++ )
            {
              sb.Append( string.Format( ValStr, next, indent, attrValToString( attrVal.L[i], 0 ) ) );
              next = ",";
            }
          }
        }
        return ( sb.ToString() );
      }
      return ( "" );
    }

    public static string attrListToLines( List<AttributeValue> attrList,
                                          int startIndex,string indent )
    {
      StringBuilder sb = new StringBuilder();
      string str;

      for( int i = startIndex; i < attrList.Count; i++ )
      {
        str = attrValToLines( attrList[i], 0, indent );
        if( str != null )
        {
          if( i > 0 )
            sb.Append( CommaSep );
          sb.Append( str );
        }
      }
      return ( sb.ToString( ) );
    }

    public static int attrValLength( AttributeValue attrVal )
    {
      if( attrVal.L.Count > 0 )
        return ( attrVal.L.Count );
      if( attrVal.SS.Count > 0 )
        return ( attrVal.SS.Count );
      if( attrVal.NS.Count > 0 )
        return ( attrVal.NS.Count );
      if( ( attrVal.S != null ) || ( attrVal.N != null ) )
        return ( 1 );
      return ( 0 );
    }

    public static string movieRunTime( int runSeconds )
    {
      StringBuilder sb = new StringBuilder();
      int hours, minutes;

      hours = runSeconds / 3600;
      runSeconds %= 3600;
      minutes = runSeconds / 60;
      sb.Append( hours.ToString( ) );
      sb.Append( " hour" );
      if( ( hours == 0 ) || ( hours > 1 ) )
        sb.Append( "s" );
      sb.Append( " and " );
      sb.Append( minutes.ToString( ) );
      sb.Append( " minute" );
      if( ( minutes == 0 ) || ( minutes > 1 ) )
        sb.Append( "s" );
      return ( sb.ToString( ) );
    }
  }
}
// snippet-end:[dynamodb.dotNET.CodeExample.00b_DDB_Attributes]